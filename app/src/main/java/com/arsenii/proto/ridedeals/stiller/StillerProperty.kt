package com.arsenii.proto.ridedeals.stiller

open class StillerProperty(
        private var value: StillerObject = StillerObject { "value" to null },
        private val muttable: Boolean = true
) {

    private lateinit var id: String

    fun isMuttable() = this.muttable

    init {

        StillerApi.addProp( this )
    }

    open fun get(): StillerObject {

        return this.value
    }

    open fun set( value: StillerObject ) {

        if( this.muttable ) {

            this.value.put( "value", value[ "value" ] )
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
