package com.m.cenarius.Web.Interceptor

import android.content.Context
import android.text.TextUtils

import com.m.cenarius.Route.Route
import com.m.cenarius.Utils.UrlUtil

/**
 * Created by m on 2017/5/10.
 */

class RouteInterceptor : InterceptorAdapter {

    override fun perform(url: String, controller: Context): Boolean {
        if (TextUtils.equals(UrlUtil.getScheme(url), Interceptor.scheme) && TextUtils.equals(UrlUtil.getHost(url), "route")) {
            Route.open(url, controller)
            return true
        }
        return false
    }
}
