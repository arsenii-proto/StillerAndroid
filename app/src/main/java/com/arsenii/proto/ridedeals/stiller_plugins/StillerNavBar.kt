package com.arsenii.proto.ridedeals.stiller_plugins

import android.content.Context
import com.arsenii.proto.ridedeals.stiller.StillerApi
import com.arsenii.proto.ridedeals.stiller.StillerPluginInterface
import com.arsenii.proto.ridedeals.stiller.StillerProperty
import com.github.salomonbrys.kotson.jsonObject
import com.google.gson.JsonObject
import android.view.Display
import android.R.attr.y
import android.R.attr.x
import android.graphics.Point
import android.os.Build
import android.view.WindowManager
import java.lang.reflect.InvocationTargetException


class StillerNavBar: StillerPluginInterface {

    override val name: String?
        get() = "navbar"

    override fun start() {

        val navbar = jsonObject(

            "height" to object: StillerProperty(){

                override fun get(): JsonObject {

                    if( StillerApi.hasConfig( "context" ) ) {

                        val context = StillerApi.getConfig( "context" ) as Context

                        val point = getNavigationBarSize(context)

                        return jsonObject("x" to point.x, "y" to point.y)


                    }

                    return jsonObject("value" to 0)
                }

            }.alias()
        )


        StillerApi.putInitialProperty(this.name as String, StillerProperty(navbar, false))
    }

    fun getNavigationBarSize(context: Context): Point {

        val appUsableSize = getAppUsableScreenSize(context)
        val realScreenSize = getRealScreenSize(context)

        // navigation bar on the side
        if (appUsableSize.x < realScreenSize.x) {
            return Point(realScreenSize.x - appUsableSize.x, appUsableSize.y)
        }

        // navigation bar at the bottom
        return if (appUsableSize.y < realScreenSize.y) {
            Point(appUsableSize.x, realScreenSize.y - appUsableSize.y)
        } else Point()

        // navigation bar is not present
    }

    fun getAppUsableScreenSize(context: Context): Point {

        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()

        display.getSize(size)

        return size
    }

    fun getRealScreenSize(context: Context): Point {

        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()

        if (Build.VERSION.SDK_INT >= 17) {

            display.getRealSize(size)

        } else if (Build.VERSION.SDK_INT >= 14) {

            try {

                size.x = Display::class.java.getMethod("getRawWidth").invoke(display) as Int
                size.y = Display::class.java.getMethod("getRawHeight").invoke(display) as Int

            } catch (e: IllegalAccessException) {

            } catch (e: InvocationTargetException) {

            } catch (e: NoSuchMethodException) {

            }

        }

        return size
    }
}