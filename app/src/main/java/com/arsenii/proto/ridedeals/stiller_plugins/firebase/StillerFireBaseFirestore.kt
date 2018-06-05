package com.arsenii.proto.ridedeals.stiller_plugins.firebase

import com.arsenii.proto.ridedeals.stiller.StillerApi
import com.arsenii.proto.ridedeals.stiller.StillerBinder
import com.arsenii.proto.ridedeals.stiller.StillerMethod
import com.arsenii.proto.ridedeals.stiller.StillerProperty
import com.github.salomonbrys.kotson.jsonObject
import com.github.salomonbrys.kotson.put
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken

object StillerFireBaseFirestore {

    val jsonProperty: JsonObject = jsonObject(

        "collection" to object: StillerMethod() {

            override fun handle(arg: JsonObject): JsonObject {

                if( arg.has("name") || arg.has("value") ) {

                    val name = if ( arg.has("name") ) arg.get("name").asString else arg.get("value").asString

                    if(! collections.containsKey(name) ) {

                        val coll = FbCollection( firestore.collection( name ) )

                        collections.put(name, coll)
                    }

                    return collections.get(name)!!.json()
                }

                return jsonObject("result" to null)
            }

        }.alias()
    )

    private val firestore = FirebaseFirestore.getInstance()
    private val collections = mutableMapOf<String, FbCollection>()


    private class FbCollection(private val collection: CollectionReference) {

        private val docs = mutableMapOf<String, FbDoc>()

        private val property = jsonObject(

            "add" to object: StillerMethod(true){}.alias(),
            "doc" to object: StillerMethod(){

                override fun handle(arg: JsonObject): JsonObject {

                    if( arg.has("name") || arg.has("value") ) {

                        val name = if ( arg.has("name") ) arg.get("name").asString else arg.get("value").asString

                        if(! docs.containsKey( name ) ) {

                            val docRef = FbDoc( collection.document( name ) )

                            docs.put(name, docRef)
                        }

                        return docs.get(name)!!.json()

                    }

                    return jsonObject("result" to null)
                }

            }.alias(),
            "where" to object: StillerMethod(){}.alias(),
            "onSnapshot" to object: StillerMethod(true){}.alias()

        )

        fun json() = property
    }

    private class FbDoc(private val docRef: DocumentReference) {

        private val property = jsonObject(

            "set" to object: StillerMethod(true){

                override fun perform(pid: String, arg: JsonObject) {

                    docRef.set(getMapOf(arg)).addOnCompleteListener(OnCompleteListener {

                        if( it.isSuccessful ) {

                            StillerApi.resolvePromise(pid, jsonObject())

                        } else {

                            StillerApi.rejectPromise(pid, jsonObject())
                        }

                    })

                }

            }.alias(),
            "delete" to object: StillerMethod(true){

                override fun perform(pid: String, arg: JsonObject) {

                    docRef.delete().addOnCompleteListener(OnCompleteListener {

                        if( it.isSuccessful ) {

                            StillerApi.resolvePromise(pid, jsonObject())

                        } else {

                            StillerApi.rejectPromise(pid, jsonObject())
                        }

                    })
                }

            }.alias(),
            "update" to object: StillerMethod(true){

                override fun perform(pid: String, arg: JsonObject) {

                    docRef.update(getMapOf(arg)).addOnCompleteListener(OnCompleteListener {

                        if( it.isSuccessful ) {

                            StillerApi.resolvePromise(pid, jsonObject())

                        } else {

                            StillerApi.rejectPromise(pid, jsonObject())
                        }

                    })
                }

            }.alias(),
            "get" to object: StillerMethod(true){

                override fun perform(pid: String, arg: JsonObject) {

                    docRef.get().addOnCompleteListener(OnCompleteListener {

                        if( it.isSuccessful ) {

                            var json = jsonObject()

                            if( it.result.data != null ) {

                                it.result.data?.forEach { json.put(it) }

                            }

                            StillerApi.resolvePromise(pid, jsonObject(
                                "exists" to it.result.exists(),
                                "data" to json
                            ))

                        } else {

                            StillerApi.rejectPromise(pid, jsonObject())
                        }
                    })
                }

            }.alias(),
            "snapshot" to object: StillerBinder(){

                private lateinit var registration: ListenerRegistration


                override fun start() {

                    registration = docRef.addSnapshotListener(EventListener { snap, e ->

                        if( e != null ) {

                            dispatch(jsonObject())

                        } else {

                            dispatch(FbSnapshot( snap ).json())
                        }

                    })
                }

                override fun stop() {

                    if( this::registration.isInitialized ) {

                        registration.remove()
                    }
                }

            }.alias()

        )

        fun json() = property
    }

    private class Query() {

        private val property: StillerProperty = StillerProperty(jsonObject(

                "where" to object: StillerProperty(){},
                "orderBy" to object: StillerProperty(){},
                "limit" to object: StillerProperty(){},
                "get" to object: StillerMethod(true){},
                "onSnapshot" to object: StillerMethod(true){}

        ))

        fun alias() = property.alias()
    }

    private class FbSnapshot(private val snap: DocumentSnapshot?) {

        private val property = jsonObject(

            "length" to object: StillerProperty(){}.alias(),
            "first" to object: StillerProperty(){}.alias(),
            "last" to object: StillerProperty(){}.alias(),
            "next" to object: StillerMethod(true){}.alias(),
            "prev" to object: StillerMethod(true){}.alias(),
            "isLast" to object: StillerMethod(true){}.alias(),
            "index" to object: StillerMethod(true){}.alias(),
            "changeType" to object: StillerMethod(true){}.alias(),
            "onSnapshot" to object: StillerMethod(true){}.alias()

        )

        fun json() = property
    }

    private fun getMapOf(json: JsonObject): Map<String,Any?> {

        val data: MutableMap<String, Any?> = Gson().fromJson(json.toString(), object: TypeToken<MutableMap<String,Any>>() {}.type)

        return data
    }
}