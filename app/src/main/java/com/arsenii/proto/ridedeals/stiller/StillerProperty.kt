package com.arsenii.proto.ridedeals.stiller

import com.github.salomonbrys.kotson.jsonObject
import com.google.gson.JsonObject

open class StillerProperty(
        private var value: JsonObject = jsonObject( "value" to null ),
        private val muttable: Boolean = true
) {

    private lateinit var id: String

    fun isMuttable() = this.muttable

    init {

        StillerApi.addProp( this )
    }

    open fun get(): JsonObject {

        return this.value
    }

    open fun set( value: JsonObject ) {

        if( this.muttable ) {

            this.value.add( "value", value[ "value" ] )
        }

    }

    fun assignId(id: String) {

        if(! this::id.isInitialized ) {

            this.id = id
        }
    }

    fun ID() = this.id

    fun alias() = "@prop(${this.id})"
}
