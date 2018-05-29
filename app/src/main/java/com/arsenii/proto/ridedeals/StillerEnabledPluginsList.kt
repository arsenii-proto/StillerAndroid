package com.arsenii.proto.ridedeals

import com.arsenii.proto.ridedeals.stiller.StillerObject
import com.arsenii.proto.ridedeals.stiller.StillerPluginInterface

object StillerEnabledPluginsList {

    val plugins = listOf<StillerPluginInterface>(

    )

    fun startAll() {

        this.plugins.forEach { it.start() }
    }
}