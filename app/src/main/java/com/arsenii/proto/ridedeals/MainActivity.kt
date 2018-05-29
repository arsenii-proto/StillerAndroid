package com.arsenii.proto.ridedeals

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.arsenii.proto.ridedeals.stiller.StillerApi
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        StillerApi.putConfig("context", this)
        StillerApi.putConfig("web_app_wrapper", web_wrapper)
        StillerApi.putConfig("web_app_url", "filmix.co")
        StillerApi.putConfig("web_app_server", "aloha")
        StillerApi.putConfig("web_app_client", "margarita")
        StillerApi.putConfig("url_whitelist", listOf( "gstatic.com" ))

        StillerEnabledPluginsList.startAll()
        StillerApi.init()


    }

}
