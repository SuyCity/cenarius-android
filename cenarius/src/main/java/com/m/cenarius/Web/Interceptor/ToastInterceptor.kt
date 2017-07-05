package com.m.cenarius.Web.Interceptor

import android.content.Context
import android.text.TextUtils
import com.m.cenarius.Extension.*

import es.dmoral.toasty.Toasty

/**
 * Created by m on 2017/5/10.
 */

class ToastInterceptor : InterceptorAdapter {

    override fun perform(url: String, controller: Context): Boolean {
        if (TextUtils.equals(url.getScheme(), Interceptor.scheme) && TextUtils.equals(url.getHost(), "toast")) {
            Toasty.normal(controller, url.getParamsJsonObject().getString("text")).show()
            return true
        }
        return false
    }
}
