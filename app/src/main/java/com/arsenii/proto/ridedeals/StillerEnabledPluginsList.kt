package com.arsenii.proto.ridedeals

import com.arsenii.proto.ridedeals.stiller.StillerPluginInterface
import com.arsenii.proto.ridedeals.stiller_plugins.StillerToast

object StillerEnabledPluginsList {

    val plugins = listOf<StillerPluginInterface>(

            StillerToast()
    )

    fun startAll() {

        this.plugins.forEach { it.start() }
    }
}