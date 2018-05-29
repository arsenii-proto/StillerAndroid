package com.arsenii.proto.ridedeals.stiller

import org.json.JSONObject

class StillerObject(): JSONObject() {

    constructor(init: StillerObject.() -> Unit) : this() {

        this.init()
    }

    infix fun <T> String.To(value: T) {

        put(this, value)
    }

}