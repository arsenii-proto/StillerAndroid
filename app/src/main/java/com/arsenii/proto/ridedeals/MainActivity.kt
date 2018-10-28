package com.arsenii.proto.ridedeals

import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.arsenii.proto.ridedeals.stiller.StillerApi
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ) {

            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        initStiller()
    }

    fun initStiller() {

        StillerApi.putConfig("context", this)
        StillerApi.putConfig("web_app_wrapper", web_wrapper)
        StillerApi.putConfig("web_app_url", "http://192.168.25.102:8080")
//        StillerApi.putConfig("web_app_url", "file:///android_asset/index.html")
        StillerApi.putConfig("web_app_server", "stiller")
        StillerApi.putConfig("web_app_client", "stiller")
        StillerApi.putConfig("url_whitelist", listOf( "gstatic.com" ))

        StillerEnabledPluginsList.startAll()
        StillerApi.init()
    }

    override fun onBackPressed() {
        
        if ( web_wrapper.canGoBack() ) {

            web_wrapper.goBack()

        } else {

            super.onBackPressed()
        }
    }

}
