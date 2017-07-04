package com.m.cenarius.Route

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.m.cenarius.Native.Cenarius
import com.m.cenarius.Utils.UrlUtil
import java.util.TreeMap

/**
 * Created by m on 2017/5/8.
 */

class Route {
    private val routes = TreeMap<String, Class<*>>()

    companion object {

        private val sharedInstance = Route()

        fun register(path: String, controller: Class<*>) {
            sharedInstance.routes.put(path, controller)
        }

        @JvmOverloads fun open(url: String, from: Context?, extraJsonString: String? = null) {
            val toControllerType = sharedInstance.routes[UrlUtil.getPath(url)]
            if (toControllerType != null) {
                val queryParameters = UrlUtil.getParameters(url)
                val fromViewController = from ?: Cenarius.context
                val intent = Intent(fromViewController, toControllerType)
                if (extraJsonString != null) {
                    intent.putExtra("params", extraJsonString)
                } else {
                    val params = UrlUtil.getParamsJsonString(url)
                    intent.putExtra("params", params)
                }
                fromViewController.startActivity(intent)
            }
        }

        fun getParamsJsonObject(activity: Activity): JSONObject {
            val bundle = activity.intent.extras
            return JSON.parseObject(bundle.getString("params"))
        }
    }
}
