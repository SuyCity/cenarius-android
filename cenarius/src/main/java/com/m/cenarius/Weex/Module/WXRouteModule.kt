package com.m.cenarius.Weex.Module

import com.m.cenarius.Route.Route
import com.taobao.weex.annotation.JSMethod
import com.taobao.weex.common.WXModule

/**
 * Created by tuotuo on 2017/7/10.
 */
class WXRouteModule : WXModule() {

    @JSMethod
    fun open(url: String) {
        Route.open(url, mWXSDKInstance.context)
    }
}