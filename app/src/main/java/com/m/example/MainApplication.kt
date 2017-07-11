package com.m.example

import android.app.Application

import com.m.cenarius.Native.Cenarius
import com.m.cenarius.Route.Route
import com.m.cenarius.Update.UpdateManager
import com.m.cenarius.Web.Interceptor.Interceptor
import com.m.cenarius.Web.Interceptor.RouteInterceptor
import com.m.cenarius.Web.Interceptor.ToastInterceptor
import com.m.cenarius.Web.Interceptor.WebViewActivity
import com.m.cenarius.Weex.Weex
import com.m.cenarius.Weex.WeexActivity

/**
 * 注意要在Manifest中设置android:name=".MainApplication"
 * 要实现ImageAdapter 否则图片不能下载
 * gradle 中一定要添加一些依赖，否则初始化会失败。
 * compile 'com.android.support:recyclerview-v7:23.1.1'
 * compile 'com.android.support:support-v4:23.1.1'
 * compile 'com.android.support:appcompat-v7:23.1.1'
 * compile 'com.alibaba:fastjson:1.1.45'
 */
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initCenarius()
        registerRoute()
        registerInterceptor()
        Weex.initWeex(this)
    }

    internal fun initCenarius() {
        Cenarius.initialize(this)
        UpdateManager.serverUrl = "https://emcs-dev.infinitus.com.cn/h5/www3.0"
    }

    internal fun registerRoute() {
        Route.register("/user", UserActivity::class.java)
        Route.register("/sign", OpenApiActivity::class.java)
        Route.register("/weex", WeexActivity::class.java)
        Route.register("/webView", WebViewActivity::class.java)
    }

    internal fun registerInterceptor() {
        Interceptor.register(RouteInterceptor())
        Interceptor.register(ToastInterceptor())
    }

}
