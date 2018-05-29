package com.arsenii.proto.ridedeals.stiller_plugins

import android.content.Context
import android.widget.Toast
import com.arsenii.proto.ridedeals.stiller.*

class StillerToast: StillerPluginInterface {

    override val name: String?
        get() = "toast"

    override fun start() {

        val toast = StillerObject{

            "short" to object: StillerMethod(){

                override fun handle(arg: StillerObject): StillerObject {

                    if( StillerApi.hasConfig( "context" ) ) {

                        val context = StillerApi.getConfig( "context" ) as Context
                        var text    = "Text missing"

                        if( arg.has( "text" ) ){

                            text = arg.getString( "text" )
                        }

                        Toast.makeText( context, arg.get( "text" ).toString(), Toast.LENGTH_SHORT).show()
                    }

                    return StillerObject()
                }

            }.alias()
        }

        StillerApi.putInitialProperty(this.name as String, StillerProperty(toast))
    }
}