package com.m.cenarius.Weex.Module

import android.text.TextUtils
import com.alibaba.fastjson.JSON
import com.m.cenarius.Extension.jsonToParameters
import com.m.cenarius.Network.HTTPHeaders
import com.m.cenarius.Network.HTTPMethod
import com.m.cenarius.Network.Network
import com.m.cenarius.Network.Parameters

import com.taobao.weex.annotation.JSMethod
import com.taobao.weex.bridge.JSCallback
import com.taobao.weex.common.WXModule
import com.taobao.weex.http.Status
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * Created by m on 2017/6/28.
 */

class WXNetworkModule : WXModule() {

    @JSMethod
    fun request(options: Map<String, Any>, callBack: JSCallback?) {
        val url = options["url"] as? String ?: ""
        var method = HTTPMethod.GET
        val m = options["method"] as? String
        if (TextUtils.equals(m?.toUpperCase(), "POST")) {
            method = HTTPMethod.POST
        }

        var parameters: Parameters? = null
        val body = options["body"] as? String
        if (body != null) {
            parameters = body.jsonToParameters()
        }

        val headers = options["headers"] as? HTTPHeaders

        val callbackResponse: MutableMap<String, Any> = TreeMap()

        Network.request(url, method, parameters, headers, object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val statusCode = response.code()
                callbackResponse["status"] = statusCode
                callbackResponse["statusText"] = Status.getStatusText(statusCode.toString())
                callbackResponse["headers"] = response.headers()
                if (response.isSuccessful) {
                    callbackResponse["ok"] = true
                    val data = response.body().string()
                    val responseType = options["type"] as? String
                    if (responseType == "json") {
                        callbackResponse["data"] = JSON.parse(data)
                    }
                } else {
                    callbackResponse["ok"] = false
                }
                callBack?.invoke(callbackResponse)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                val message = t.message
                if (message != null) {
                    callbackResponse["statusText"] = message
                }
                callbackResponse["ok"] = false
                callBack?.invoke(callbackResponse)
            }
        })
    }
}
