package com.m.cenarius.Weex.Module

import android.text.TextUtils
import com.m.cenarius.Extension.jsonToParameters

import com.m.cenarius.Network.Network
import com.taobao.weex.WXSDKEngine
import com.taobao.weex.annotation.JSMethod
import com.taobao.weex.common.WXModule

/**
 * Created by m on 2017/6/28.
 */

class WXNetworkModule : WXModule() {

    @JSMethod
    fun request(options: Map<String, Any>, callBackId: String) {
        val url = options["url"] as? String ?: ""
        var method = Network.HTTPMethod.GET
        val m = options["method"] as? String
        if (TextUtils.equals(m, "POST")) {
            method = Network.HTTPMethod.POST
        }

        var parameters: Map<String, Any>?
        val body = options["body"] as? String
        if (body != null) {
            parameters = body.jsonToParameters()
        }

        //        Network.requset()
        WXSDKEngine.callback(mWXSDKInstance.instanceId, callBackId, options)
    }
}
