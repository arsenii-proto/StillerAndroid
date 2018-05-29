package com.arsenii.proto.ridedeals.stiller

import android.net.Uri
import android.os.Build
import android.util.Log
import android.webkit.*
import com.github.salomonbrys.kotson.jsonObject
import com.github.salomonbrys.kotson.toJson
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.util.*

object StillerApi {

    private val config      = mutableMapOf<String,Any?>()
    private val props       = mutableMapOf<String,StillerProperty>()
    private val methods     = mutableMapOf<String,StillerMethod>()
    private val promises    = mutableListOf<String>()
    private val hooks       = mutableMapOf<String, MutableList<Runnable>>()
    private val initial: JsonObject = jsonObject (
        "isAndroid" to true,
        "isIOS" to false
    )

    fun putConfig(key: String, value: Any? ) {

        this.config.put( key, value )
    }

    fun getConfig(key: String, default: Any? = null): Any? {

        if( this.hasConfig( key ) ) {

            return this.config.get( key )

        } else {

            return default
        }
    }

    fun hasConfig(key: String ) = this.config.containsKey( key )

    fun addProp( prop: StillerProperty ) {

        var id = "${this.tokinize()}"

        while( this.props.containsKey( id ) )
            id = "${this.tokinize()}"

        this.props.put( id, prop )

        prop.assignId( id )
    }

    fun addMethod( method: StillerMethod ) {

        var id = "${this.tokinize()}"

        while( this.methods.containsKey( id ) )
            id = "${this.tokinize()}"

        this.methods.put( id, method )

        method.assignId( id )
    }

    fun addHook(type: String, run: Runnable) {

        if(! this.hooks.containsKey( type ) ) {

            this.hooks.put( type, mutableListOf() )
        }

        this.hooks.get( type )!!.add( run )
    }

    fun handleHooks( type: String ) {

        if( this.hooks.containsKey( type ) ) {

            this.hooks.get( type )!!.forEach { it.run() }
        }
    }

    fun putInitialProperty( prop: String, value: StillerProperty ) {

        this.initial.add( prop, value.alias().toJson() )
    }

    fun putInitialMethod( name: String, method: StillerMethod ) {

        this.initial.add( name, method.alias().toJson() )
    }

    fun resolvePromise( pid: String, arg: JsonObject ) {

        if( this.hasConfig("web_app_wrapper") ) {

            val wrapper = this.getConfig("web_app_wrapper") as WebView
            val web_app = this.getConfig("web_app_client") as String

            wrapper.post {

                wrapper.evaluateJavascript("(() => {" +
                        "" +
                        "$web_app.resolvePromise('$pid', '$arg')" +
                        "" +
                        "})()", ValueCallback {

                })
            }
        }
    }

    fun rejectPromise( pid: String, arg: JsonObject ){

        if( this.hasConfig("web_app_wrapper") && this.hasConfig("web_app_client") ) {

            val wrapper = this.getConfig("web_app_wrapper") as WebView
            val web_app = this.getConfig("web_app_client") as String

            wrapper.post {

                wrapper.evaluateJavascript("(() => {" +
                        "" +
                        "$web_app.rejectPromise('$pid', '$arg')" +
                        "" +
                        "})()", ValueCallback {

                })
            }
        }
    }

    fun init() {

        if( this.hasConfig("web_app_wrapper") && this.hasConfig("web_app_url") && this.hasConfig("web_app_server") ) {

            val wrapper = this.getConfig("web_app_wrapper") as WebView
            val web_url = this.getConfig("web_app_url") as String
            val web_app = this.getConfig("web_app_server") as String

            this.handleHooks("before-init")

            wrapper.settings.javaScriptEnabled = true
            wrapper.addJavascriptInterface(StillerWebBridge(), web_app)
            Log.i("ADST", "web_app: $web_app")

            wrapper.webViewClient = WebViewClient()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true);
            }

            wrapper.loadUrl("")
            val uri: Uri
            try {
                uri = buildUri(web_url)
                wrapper.loadUrl(uri.toString())
                Log.i("ADST", "web_url: $web_url")
            } catch(e: UnsupportedOperationException) {
                e.printStackTrace()
            }

            wrapper.loadUrl("javascript:alert('bleaha');")
            wrapper.post {

                wrapper.evaluateJavascript("alert('Bleadi')", ValueCallback {

                })
            }

            this.handleHooks("after-init")
        }
    }

    @Throws(UnsupportedOperationException::class)
    private fun buildUri(authority: String): Uri {
        val builder = Uri.Builder()
        builder.scheme("https")
                .authority(authority)
        return builder.build()
    }

    private fun tokinize(): String {

        val alfanum = "ACEFGHJKLMNPQRUVWXYabcdefhijkprstuvwx0123456789"
        var token   = ""

        for ( i in 1..8 ) {

            token += alfanum[ Random().nextInt( alfanum.length-1 ) ]
        }

        return token
    }


    class StillerWebBridge {



        @JavascriptInterface
        fun debug(){

            val debp = StillerApi.props
            val debr = StillerApi.promises
            val debm = StillerApi.methods

            // Make debuger point here
            val a = 1
        }

        @JavascriptInterface
        fun initial() = StillerApi.initial.toString()

        @JavascriptInterface
        fun getProp(id: String): String {

            if (StillerApi.props.containsKey(id)) {

                return StillerApi.props.get(id)!!.get().toString()

            } else {

                return jsonObject ( "value" to "@undefined" ).toString()
            }

        }

        @JavascriptInterface
        fun setProp(id: String, arg: String) {

            if (StillerApi.props.containsKey(id)) {

                StillerApi.props.get(id)!!.set(Gson().fromJson(arg, JsonObject::class.java))
            }

        }

        @JavascriptInterface
        fun callMethod(id: String, arg: String): String {

            if (StillerApi.methods.containsKey(id)) {

                val method = StillerApi.methods.get(id)

                if (method!!.isPromise()) {

                    var pid = "${StillerApi.tokinize()}"

                    while (StillerApi.promises.contains(id))
                        pid = "${StillerApi.tokinize()}"

                    method.perform(pid, Gson().fromJson(arg, JsonObject::class.java))

                    return jsonObject ("result" to "@promise($pid)").toString()

                } else {

                    return method.handle(Gson().fromJson(arg, JsonObject::class.java)).toString()
                }

            } else {

                return jsonObject ("result" to "@undefined").toString()
            }
        }
    }

}