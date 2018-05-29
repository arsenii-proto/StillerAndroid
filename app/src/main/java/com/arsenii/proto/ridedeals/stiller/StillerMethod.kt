package com.arsenii.proto.ridedeals.stiller

import com.github.salomonbrys.kotson.jsonObject
import com.google.gson.JsonObject

open class StillerMethod(
        private val promise: Boolean = false
    ) {

    private lateinit var id: String

    init {

        StillerApi.addMethod( this )
    }

    fun isPromise() = this.promise

    fun assignId(id: String) {

        if(! this::id.isInitialized ) {

            this.id = id
        }
    }

    fun ID() = this.id

    open fun handle( arg: JsonObject ): JsonObject {

        return jsonObject( "value" to null )
    }

    open fun perform( pid: String, arg: JsonObject ) {

        StillerApi.resolvePromise( pid, arg )
    }


    fun alias() = "@method(${this.id})"
}