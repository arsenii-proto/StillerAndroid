package com.arsenii.proto.ridedeals.stiller_plugins

import android.content.Context
import com.arsenii.proto.ridedeals.stiller.StillerApi
import com.arsenii.proto.ridedeals.stiller.StillerPluginInterface
import com.arsenii.proto.ridedeals.stiller.StillerProperty
import com.arsenii.proto.ridedeals.stiller_plugins.firebase.StillerFireBaseAuth
import com.arsenii.proto.ridedeals.stiller_plugins.firebase.StillerFireBaseFirestore
import com.github.salomonbrys.kotson.jsonObject
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.JsonObject

class StillerFireBase: StillerPluginInterface {

    override val name: String?
        get() = "toast"

    override fun start() {


        if( StillerApi.hasConfig( "context" ) ) {

            val context = StillerApi.getConfig("context") as Context

            FirebaseApp.initializeApp(context)

            val firebase = jsonObject(

                "app_token" to object: StillerProperty(){

                    override fun get(): JsonObject {

                        return jsonObject(
                            "value" to FirebaseInstanceId.getInstance().token
                        )
                    }

                }.alias(),
                "auth" to StillerProperty(StillerFireBaseAuth.jsonProperty).alias(),
                "firestore" to StillerProperty(StillerFireBaseFirestore.jsonProperty).alias()

            )

            StillerApi.putInitialProperty("firebase", StillerProperty(firebase, false))
        }
    }
}