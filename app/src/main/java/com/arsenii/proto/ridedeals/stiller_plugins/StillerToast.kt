package com.arsenii.proto.ridedeals.stiller_plugins

import android.content.Context
import android.widget.Toast
import com.arsenii.proto.ridedeals.stiller.*
import com.github.salomonbrys.kotson.jsonObject
import com.google.gson.JsonObject

class StillerToast: StillerPluginInterface {

    override val name: String?
        get() = "toast"

    override fun start() {

        val toast = jsonObject(

            "short" to object: StillerMethod(){

                override fun handle(arg: JsonObject): JsonObject {

                    if( StillerApi.hasConfig( "context" ) ) {

                        val context = StillerApi.getConfig( "context" ) as Context
                        var text    = "Text missing"

                        if( arg.has( "text" ) ){

                            text = arg.get( "text" ).asString
                        }

                        val tt = "asdf"

                        Toast.makeText( context, text, Toast.LENGTH_SHORT).show()
                    }

                    return jsonObject()
                }

            }.alias()

        )


        StillerApi.putInitialProperty(this.name as String, StillerProperty(toast))
    }

}