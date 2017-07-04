package com.m.cenarius.Web.Interceptor

import android.content.Context

import java.util.ArrayList

/**
 * Created by m on 2017/5/10.
 */

class Interceptor {

    private val interceptors = ArrayList<InterceptorAdapter>()

    companion object {

        val scheme = "cenarius"
        private val sharedInstance = Interceptor()

        fun register(interceptor: InterceptorAdapter) {
            sharedInstance.interceptors.add(interceptor)
        }

        fun perform(url: String, controller: Context): Boolean {
            for (interceptor in sharedInstance.interceptors) {
                if (interceptor.perform(url, controller)) {
                    return true
                }
            }
            return false
        }
    }
}
