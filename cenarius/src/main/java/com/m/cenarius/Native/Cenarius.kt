package com.m.cenarius.Native

import android.app.Application
import android.content.Context
import com.m.cenarius.Web.Interceptor.Interceptor
import com.m.cenarius.Web.Interceptor.RouteInterceptor
import com.m.cenarius.Web.Interceptor.ToastInterceptor
import com.m.cenarius.Weex.Adapter.ImageAdapter
import com.m.cenarius.Weex.Component.WXRichTextComponent
import com.m.cenarius.Weex.Module.WXEventModule
import com.m.cenarius.Weex.Module.WXNetworkModule
import com.m.cenarius.Weex.Module.WXProgressHUDModule
import com.m.cenarius.Weex.Module.WXRouteModule
import com.taobao.weex.InitConfig
import com.taobao.weex.WXSDKEngine
import com.taobao.weex.utils.WXLogUtils
import io.realm.Realm

/**
 * Created by m on 2017/7/3.
 */
open class Cenarius {

    companion object {

        lateinit var context: Context

        fun initCenarius(application: Application) {
            context = application.applicationContext
            Realm.init(context)
            registerInterceptor()
            initWeex(application)
        }

        private fun registerInterceptor() {
            Interceptor.register(RouteInterceptor())
            Interceptor.register(ToastInterceptor())
        }

        private fun initWeex(application: Application) {
            val config = InitConfig.Builder().setImgAdapter(ImageAdapter()).build()
            WXSDKEngine.initialize(application, config)

            WXSDKEngine.registerModule("event", WXEventModule::class.java)
            WXSDKEngine.registerModule("network", WXNetworkModule::class.java)
            WXSDKEngine.registerModule("route", WXRouteModule::class.java)
            WXSDKEngine.registerModule("progressHUD", WXProgressHUDModule::class.java)

            WXSDKEngine.registerComponent("select", WXRichTextComponent::class.java)
        }
    }
}