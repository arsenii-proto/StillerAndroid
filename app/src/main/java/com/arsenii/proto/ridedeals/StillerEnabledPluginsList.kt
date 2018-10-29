package com.arsenii.proto.ridedeals

import com.arsenii.proto.ridedeals.stiller_plugins.*

object StillerEnabledPluginsList {

    val plugins = listOf(

        StillerToast(),
        StillerSplashScreen(),
        StillerFireBase(),
        StillerSizes(),
        StillerLocation()
    )

    fun startAll() {

        this.plugins.forEach { it.start() }
    }
}