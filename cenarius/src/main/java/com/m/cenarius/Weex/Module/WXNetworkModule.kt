package com.m.cenarius.Weex.Module

import android.text.TextUtils
import com.m.cenarius.Extension.jsonToParameters
import com.m.cenarius.Network.HTTPHeaders
import com.m.cenarius.Network.HTTPMethod
import com.m.cenarius.Network.Network
import com.m.cenarius.Network.Parameters

import com.taobao.weex.WXSDKEngine
import com.taobao.weex.annotation.JSMethod
import com.taobao.weex.common.WXModule
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
    fun request(options: Map<String, Any>, callBackId: String) {
        val url = options["url"] as? String ?: ""
        var method = HTTPMethod.GET
        val m = options["method"] as? String
        if (TextUtils.equals(m, "POST")) {
            method = HTTPMethod.POST
        }

        var parameters: Parameters? = null
        val body = options["body"] as? String
        if (body != null) {
            parameters = body.jsonToParameters()
        }

        val headers = options["headers"] as? HTTPHeaders

        var callbackResponse: MutableMap<String, Any> = TreeMap()

        Network.request(url, method, parameters, headers, object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                callbackResponse["status"] = response.code()
                callbackResponse["statusText"] = response.message()
                callbackResponse["headers"] = response.headers()
                if (response.isSuccessful) {
                    val data = response.body().string()
                    val responseType = options["type"] as? String
                    if (responseType == "json") {
                        data.jsonToParameters()
                    }
                } else {

                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            }
        })


        WXSDKEngine.callback(mWXSDKInstance.instanceId, callBackId, options)
    }
}
