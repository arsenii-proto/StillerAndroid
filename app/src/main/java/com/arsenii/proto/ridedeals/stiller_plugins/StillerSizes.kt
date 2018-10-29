package com.arsenii.proto.ridedeals.stiller_plugins

import android.app.Activity
import android.content.Context
import com.arsenii.proto.ridedeals.stiller.StillerApi
import com.arsenii.proto.ridedeals.stiller.StillerPluginInterface
import com.arsenii.proto.ridedeals.stiller.StillerProperty
import com.github.salomonbrys.kotson.jsonObject
import com.google.gson.JsonObject


class StillerSizes: StillerPluginInterface {

    override val name: String?
        get() = "sizes"

    override fun start() {

        val sizes = jsonObject(

            "statusBarHeight" to object: StillerProperty(){

                override fun get(): JsonObject {

                    if( StillerApi.hasConfig( "context" ) ) {

                        val context = StillerApi.getConfig( "context" ) as Context


                        return jsonObject("value" to getElementSize(context, "status_bar_height"))
                    }

                    return jsonObject("value" to 0)
                }

            }.alias(),
            "navBarHeight" to object: StillerProperty(){

                override fun get(): JsonObject {

                    if( StillerApi.hasConfig( "context" ) ) {

                        val target = StillerApi.getConfig( "context" ) as Activity

                        if(!target.window.javaClass.toString().contains("Miui")){

                            return jsonObject("value" to getElementSize(target as Context, "navigation_bar_height"))
                        }
                    }

                    return jsonObject("value" to 0)
                }

            }.alias(),
            "details" to object: StillerProperty(){

                override fun get(): JsonObject {

                    if( StillerApi.hasConfig( "context" ) ) {

                        val target = StillerApi.getConfig( "context" ) as Activity

                        return jsonObject("value" to target.window.javaClass.toString())
                    }

                    return jsonObject("value" to 0)
                }

            }.alias()
        )


        StillerApi.putInitialProperty(this.name as String, StillerProperty(sizes, false))
    }

    fun getElementSize(context: Context, elementName: String): Int {

        var result = 0
        val resourceId = context.resources.getIdentifier(elementName, "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result / 2
    }
}