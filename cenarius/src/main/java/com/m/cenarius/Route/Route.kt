package com.m.cenarius.Route

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.Intent

import com.m.cenarius.Extension.*
import com.m.cenarius.Native.Cenarius
import com.m.cenarius.R

import java.util.TreeMap

/**
 * Created by m on 2017/5/8.
 */

class Route {

    private val routes: MutableMap<String, Class<*>> = TreeMap()

    companion object {

        private val sharedInstance = Route()

        fun register(path: String, controller: Class<*>) {
            sharedInstance.routes.put(path, controller)
        }

        fun open(url: String, from: Context? = null) {
            val path = url.getPath()
            val extraJsonString = url.getParamsJsonString()
            val present = url.getParameters()["present"] == "true"

            open(path, extraJsonString, from, present)
        }

        fun open(path: String, extraJsonString: String? = null, from: Context? = Cenarius.context, present: Boolean = false) {
            val toControllerType = sharedInstance.routes[path]
            if (toControllerType != null) {
                val intent = Intent(from, toControllerType)
                if (extraJsonString != null) {
                    intent.putExtra("params", extraJsonString)
                }
                from?.startActivity(intent)
                if (present) {
                    if (from is Activity) {
                        from.overridePendingTransition(R.anim.present_enter, R.anim.present_exit)
                    } else if (from is Fragment) {
                        from.activity.overridePendingTransition(R.anim.present_enter, R.anim.present_exit)
                    }
                }
            }
        }
    }
}
