package com.arsenii.proto.ridedeals.stiller_plugins.firebase

import android.util.Log
import com.arsenii.proto.ridedeals.MainActivity
import com.arsenii.proto.ridedeals.stiller.StillerApi
import com.arsenii.proto.ridedeals.stiller.StillerBinder
import com.arsenii.proto.ridedeals.stiller.StillerMethod
import com.arsenii.proto.ridedeals.stiller.StillerProperty
import com.github.salomonbrys.kotson.jsonObject
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.gson.JsonObject
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import kotlin.math.log

object StillerFireBaseAuth {

    var auth = FirebaseAuth.getInstance()
    lateinit var verificationId: String

    init {

        auth.setLanguageCode(Locale.getDefault().toString().split("_")[0])
    }

    val jsonProperty: JsonObject = jsonObject(

        "lang" to object: StillerProperty(){

            override fun get(): JsonObject {

                return jsonObject("value" to auth.languageCode)
            }

            override fun set(value: JsonObject) {

                auth.setLanguageCode( value["value"].asString )
            }

        }.alias(),

        "logged" to object: StillerProperty(){

            override fun get(): JsonObject {

                return jsonObject("value" to (auth.currentUser !== null) )
            }

        }.alias(),


        "loggedUser" to object: StillerProperty(){

            override fun get(): JsonObject {

                if( auth.currentUser !== null ) {

                    val user: FirebaseUser = auth.currentUser as FirebaseUser

                    return jsonObject(

                        "name" to user.displayName,
                        "email" to user.email,
                        "token" to object: StillerMethod(true){

                            override fun perform(pid: String, arg: JsonObject) {

                                user.getIdToken(true).addOnCompleteListener {

                                    if( it.isSuccessful ) {

                                        StillerApi.resolvePromise(pid, jsonObject("value" to it.result.token))

                                    } else {

                                        StillerApi.rejectPromise(pid, jsonObject("value" to it.result.token))
                                    }
                                }
                            }

                        }.alias(),
                        "uid" to user.uid,
                        "phone" to user.phoneNumber,
                        "photo" to user.photoUrl,
                        "anonymous" to user.isAnonymous
                    )
                }

                return jsonObject("value" to null)
            }

        }.alias(),

        "signInWithPhoneNumber" to object: StillerMethod(true) {

            override fun perform(pid: String, arg: JsonObject) {

                if( arg.has("number") || arg.has("value") ) {


                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        if (arg.has("number") ) arg.get("number").asString else arg.get("value").asString,
                        60,
                        TimeUnit.SECONDS,
                        StillerApi.getConfig("context") as MainActivity,
                        object: PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                            override fun onVerificationCompleted(p0: PhoneAuthCredential?) {

                                signInWithCredential(pid,p0)
                            }

                            override fun onVerificationFailed(p0: FirebaseException?) {

                                StillerApi.rejectPromise(pid, jsonObject("result" to "error"))
                            }

                            override fun onCodeSent(p0: String?, p1: PhoneAuthProvider.ForceResendingToken?) {
                                super.onCodeSent(p0, p1)

                                if( p0 !== null) {

                                    verificationId = p0
                                }
                            }
                        }
                    )
                }

            }

        }.alias(),

        "signInWithPhoneNumberCode" to object: StillerMethod(true){

            override fun perform(pid: String, arg: JsonObject) {

                if( ( arg.has("code") || arg.has("value") ) && this@StillerFireBaseAuth::verificationId.isInitialized ) {

                    signInWithCredential(
                        pid,
                        PhoneAuthProvider.getCredential(
                            verificationId,
                            if (arg.has("code") ) arg.get("number").asString else arg.get("value").asString
                        )
                    )
                }
            }

        }.alias(),

        "logout" to object: StillerMethod(){

            override fun handle(arg: JsonObject): JsonObject {

                auth.signOut()
                return jsonObject("result" to  "successfully")
            }

        }.alias(),

        "onLogginStateChanged" to object: StillerBinder() {

            val authStateListenner =  FirebaseAuth.AuthStateListener {
                dispatch(jsonObject(
                    "logged" to (auth.currentUser !== null)
                ))
            }

            override fun start() {

                auth.addAuthStateListener(authStateListenner)
            }

            override fun stop() {
                auth.removeAuthStateListener(authStateListenner)
            }
        }.alias()

    )

    fun signInWithCredential(pid: String, p0: PhoneAuthCredential?) {

        if( p0 != null ) {

            auth.signInWithCredential(p0).addOnCompleteListener(
                StillerApi.getConfig("context") as MainActivity
            ) {

                if( it.isSuccessful() ) {

                    StillerApi.resolvePromise(pid, jsonObject("result" to "successfully"))

                } else {

                    Log.i("ADNC", "WTF")
                    StillerApi.rejectPromise(pid, jsonObject("result" to "error"))
                }
            }

        } else {

            StillerApi.rejectPromise(pid, jsonObject("result" to "error"))
        }
    }
}