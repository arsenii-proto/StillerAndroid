package com.arsenii.proto.ridedeals.stiller_plugins

import android.app.Activity
import android.location.Address
import android.location.Location
import android.os.Bundle
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationListener
import android.location.Geocoder
import android.util.Log
import com.github.salomonbrys.kotson.jsonObject
import com.google.gson.JsonObject
import java.io.IOException
import java.util.*
import com.google.android.gms.location.LocationSettingsStatusCodes
import android.content.IntentSender
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import com.arsenii.proto.ridedeals.stiller.*
import com.google.android.gms.location.LocationSettingsRequest



class StillerLocation: StillerPluginInterface, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLocationRequest: LocationRequest? = null
    private var mLastLocation: Location? = null
    private var context: Activity? = null
    private var binderId: String? = null

    override val name: String?
        get() = "location"

    override fun start() {

        val location = jsonObject(
            "last" to object: StillerProperty(){

                override fun get(): JsonObject {

                    if( ActivityCompat.checkSelfPermission(context!!, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ){

                        ActivityCompat.requestPermissions(
                            context!!,
                            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),
                            2
                        )

                        buildGoogleApiClient()
                    }

                    if ( mLastLocation !== null ) {

                        return jsonObject(
                            "lat" to mLastLocation?.latitude,
                            "long" to mLastLocation?.longitude
                        )
                    }

                    return jsonObject("value" to null)
                }

            }.alias(),

            "updater" to object: StillerBinder(){

                override fun start() {

                    if( ActivityCompat.checkSelfPermission(context!!, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ){

                        ActivityCompat.requestPermissions(
                            context!!,
                            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),
                            2
                        )

                        buildGoogleApiClient()
                    }

                    binderId = this.ID()

                    dispatch()
                }

                override fun stop() {

                    binderId = null
                }

            }.alias(),

            "address" to object: StillerMethod() {

                override fun handle(arg: JsonObject): JsonObject {

                    val lat = if( arg.has("lat")  ) arg.get("lat").asDouble else if( mLastLocation != null ) mLastLocation?.latitude else null
                    val long = if( arg.has("long")  ) arg.get("long").asDouble else if( mLastLocation != null ) mLastLocation?.longitude else null

                    if( lat != null && long != null ) {

                        return jsonObject("value" to getAddress(lat, long))
                    }

                    return jsonObject("value" to null)
                }

            }.alias()
        )

        StillerApi.putInitialProperty(name!!, StillerProperty(location, false))
    }

    init {

        if( StillerApi.hasConfig("context") ) {

            context = StillerApi.getConfig("context") as Activity

            buildGoogleApiClient()
            createLocationRequest()
        }

    }

    private fun dispatch() {

        if (mLastLocation != null && binderId != null) {

            StillerApi.dispatchEvent(binderId!!, jsonObject(
                "lat" to mLastLocation?.latitude,
                "long" to mLastLocation?.longitude
            ))
        }
    }

    @Synchronized
    protected fun buildGoogleApiClient() {

        mGoogleApiClient = GoogleApiClient.Builder( context!! )
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()

        mGoogleApiClient?.connect()

        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = (30 * 1000).toLong()
        locationRequest.fastestInterval = (5 * 1000).toLong()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        // **************************
        builder.setAlwaysShow(true) // this is the key ingredient
        // **************************

        val result = LocationServices.SettingsApi
                .checkLocationSettings(mGoogleApiClient, builder.build())

        result.setResultCallback {

            val status = it.status
            val state = it.locationSettingsStates

            when (status.statusCode) {

                LocationSettingsStatusCodes.SUCCESS -> Log.i("ADNC","Success")
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {

                    Log.i("ADNC","GPS is not on")
                    // Location settings are not satisfied. But could be
                    // fixed by showing the user
                    // a dialog.
                    try {
                        // Show the dialog by calling
                        // startResolutionForResult(),
                        // and check the result in onActivityResult().
                        status.startResolutionForResult(context!!, 1000)

                    } catch (e: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }

                }

                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Log.i("ADNC", "Setting change not allowed")
            }

        }

    }

    protected fun createLocationRequest() {

        mLocationRequest = LocationRequest()
        mLocationRequest?.interval = 10000
        mLocationRequest?.fastestInterval = 5000
        mLocationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    protected fun startLocationUpdates() {

        try {

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)

        }catch (e: SecurityException) {

            Log.i("ADNC", "startLocationUpdates: $e")
        }
    }

    protected fun stopLocationUpdates() {

        try {

            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)


        }catch (e: SecurityException) {

            Log.i("ADNC", "stopLocationUpdates: $e")
        }
    }

    override fun onConnected(p0: Bundle?) {

        try {

            LocationServices.getFusedLocationProviderClient(context!!).lastLocation.addOnCompleteListener {

                if( it.isSuccessful ) {

                    mLastLocation = it.result

                    dispatch()

                    startLocationUpdates()
                }
            }


        }catch (e: SecurityException) {

            Log.i("ADNC", "onConnected: $e")
        }

    }

    override fun onConnectionSuspended(p0: Int) {

        stopLocationUpdates()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {

        Log.i("ADNC", "Connection Failed")
    }

    override fun onLocationChanged(location: Location?) {

        mLastLocation = location;
        dispatch()
    }

    private fun getAddress(lat: Double, long: Double): String {

        val geocoder = Geocoder(context, Locale.getDefault())
        var addresses: List<Address>? = null

        try {

            addresses = geocoder.getFromLocation(lat, long, 1)

        } catch (e1: IOException) {

            Log.e("ADNC", "IO Exception in getFromLocation()")

            e1.printStackTrace()

            return "IO Exception trying to get address"

        } catch (e2: IllegalArgumentException) {

            Log.e("LocationSampleActivity", "Illegal arguments $lat, $long  passed to address service")

            e2.printStackTrace()

            return "Illegal arguments $lat, $long  passed to address service"
        }

        // If the reverse geocode returned an address
        if (addresses != null && addresses.size > 0) {
            // Get the first address
            val address = addresses[0]
            // Return the text
            if( address.maxAddressLineIndex >= 0 ) {

                return "${address.getAddressLine(0)}"
            }

            return "${address.thoroughfare}, ${address.featureName}"

        } else {

            return "No address found"
        }
    }
}