package com.m.cenarius.Web.Interceptor

import android.content.Context
import android.text.TextUtils
import com.m.cenarius.Extension.*

import com.m.cenarius.Route.Route

/**
 * Created by m on 2017/5/10.
 */

class RouteInterceptor : InterceptorAdapter {

    override fun perform(url: String, controller: Context): Boolean {
        if (TextUtils.equals(url.getScheme(), Interceptor.scheme) && TextUtils.equals(url.getHost(), "route")) {
            Route.open(url, controller)
            return true
        }
        return false
    }
}
