package com.arsenii.proto.ridedeals.stiller

import com.github.salomonbrys.kotson.jsonObject
import com.google.gson.JsonObject

open class StillerBinder {

    private lateinit var id: String
    private var started = false

    init {

        StillerApi.addBinder( this )
    }

    fun assignId(id: String) {

        if(! this::id.isInitialized ) {

            this.id = id
        }
    }

    fun ID() = this.id

    open fun start() { started = true }
    open fun stop() { started = false }

    fun isStared() = started

    fun alias() = "@binder(${this.id})"

    fun dispatch(arg: JsonObject) {

        StillerApi.dispatchEvent(id, arg)
    }
}