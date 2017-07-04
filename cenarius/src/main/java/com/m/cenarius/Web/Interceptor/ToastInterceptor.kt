package com.m.cenarius.Web.Interceptor

import android.content.Context
import android.text.TextUtils

import com.m.cenarius.Utils.UrlUtil

import es.dmoral.toasty.Toasty

/**
 * Created by m on 2017/5/10.
 */

class ToastInterceptor : InterceptorAdapter {

    override fun perform(url: String, controller: Context): Boolean {
        if (TextUtils.equals(UrlUtil.getScheme(url), Interceptor.scheme) && TextUtils.equals(UrlUtil.getHost(url), "toast")) {
            Toasty.normal(controller, UrlUtil.getParamsJsonObject(url).getString("text")).show()
            return true
        }
        return false
    }
}
