package com.m.cenarius.Web.Interceptor

import android.content.Context

/**
 * Created by m on 2017/5/10.
 */

interface InterceptorAdapter {

    fun perform(url: String, controller: Context): Boolean
}
