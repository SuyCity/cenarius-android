package com.m.cenarius.Weex

import android.app.Application
import com.m.cenarius.Weex.Adapter.ImageAdapter
import com.m.cenarius.Weex.Component.WXRichTextComponent
import com.m.cenarius.Weex.Module.WXEventModule
import com.m.cenarius.Weex.Module.WXNetworkModule
import com.m.cenarius.Weex.Module.WXProgressHUDModule
import com.m.cenarius.Weex.Module.WXRouteModule
import com.taobao.weex.InitConfig
import com.taobao.weex.WXSDKEngine

/**
 * Created by tuotuo on 2017/7/11.
 */
class Weex {

    companion object {

        fun initWeex(application: Application) {
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