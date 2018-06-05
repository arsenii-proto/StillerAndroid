package com.arsenii.proto.ridedeals.stiller_plugins

import android.view.View
import android.webkit.WebView
import com.arsenii.proto.ridedeals.stiller.StillerApi
import com.arsenii.proto.ridedeals.stiller.StillerMethod
import com.arsenii.proto.ridedeals.stiller.StillerPluginInterface
import com.arsenii.proto.ridedeals.stiller.StillerProperty
import com.github.salomonbrys.kotson.jsonObject
import com.google.gson.JsonObject

class StillerSplashScreen: StillerPluginInterface {

    override val name: String?
        get() = "toast"

    override fun start() {

        if( StillerApi.hasConfig("web_app_wrapper") ) {

            val web_app_wrapper = StillerApi.getConfig("web_app_wrapper") as WebView

            web_app_wrapper.post {

                web_app_wrapper.visibility = View.GONE
            }

        }

        val splashScreen = jsonObject(

            "show" to object: StillerMethod(){

                override fun handle(arg: JsonObject): JsonObject {

                    if( StillerApi.hasConfig("web_app_wrapper") ) {

                        val web_app_wrapper = StillerApi.getConfig("web_app_wrapper") as WebView

                        web_app_wrapper.post {

                            web_app_wrapper.visibility = View.GONE
                        }

                        return jsonObject("result" to true)
                    }

                    return jsonObject("result" to false)
                }

            }.alias(),


            "hide" to object: StillerMethod(){

                override fun handle(arg: JsonObject): JsonObject {

                    if( StillerApi.hasConfig("web_app_wrapper") ) {

                        val web_app_wrapper = StillerApi.getConfig("web_app_wrapper") as WebView

                        web_app_wrapper.post {

                            web_app_wrapper.visibility = View.VISIBLE
                        }

                        return jsonObject("result" to true)
                    }

                    return jsonObject("result" to false)
                }

            }.alias()
        )

        StillerApi.putInitialProperty("splashScreen", StillerProperty(splashScreen, false))
    }
}