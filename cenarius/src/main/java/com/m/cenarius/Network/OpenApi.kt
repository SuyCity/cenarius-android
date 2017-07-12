package com.m.cenarius.Network

import android.content.SharedPreferences
import android.util.Base64

import com.litesuits.common.utils.HexUtil
import com.litesuits.common.utils.MD5Util
import com.litesuits.common.utils.RandomUtil
import com.m.cenarius.Extension.*
import com.m.cenarius.Native.Cenarius

import java.util.Date
import java.util.TreeMap

/**
 * Created by m on 2017/5/12.
 */

class OpenApi {
    private var accessToken = sharedPreferences.getString(accessTokenKey, null)
    private var appKey: String? = null
    private var appSecret: String? = null

    companion object {

        val xRequestKey = "X-Requested-With"
        val xRequestValue = "OpenAPIRequest"
        val contentTypeKey = "Content-Type"
        val contentTypeValue = "application/json"

        private val fileName = "TokenFile"
        private val accessTokenKey = "CenariusAccessToken"
        private val sharedInstance = OpenApi()

        fun setAccessToken(token: String?) {
            sharedInstance.accessToken = token
            editor.putString(accessTokenKey, token).commit()
        }

        fun getAccessToken(): String? {
            return sharedInstance.accessToken
        }

        fun getAppKey(): String {
            return sharedInstance.appKey!!
        }

        fun getAppSecret(): String {
            return sharedInstance.appSecret!!
        }

        fun set(appKey: String, appSecret: String) {
            sharedInstance.appKey = appKey
            sharedInstance.appSecret = appSecret
        }

        fun sign(url: String, parameters: Parameters?, headers: Map<String, String>?): String {
            var isOpenApi = false
            var isJson = false
            if (headers != null) {
                for (header in headers) {
                    if (header.key == xRequestKey && header.value == xRequestValue) {
                        isOpenApi = true
                    }
                    if (header.key == contentTypeKey && header.value.contains(contentTypeValue)) {
                        isJson = true
                    }
                }
            }

            if (!isOpenApi) {
                return url
            }

            val queryString = url.getQuery()
            var queryCombined = queryString
            var bodyString: String?
            if (parameters != null && parameters.isNotEmpty()) {
                if (isJson) {
                    bodyString = "openApiBodyString=" + parameters.toJSONString().encodeURIComponent()
                } else {
                    bodyString = parameters.toQuery()
                }
                if (queryCombined != null) {
                    queryCombined += "&" + bodyString
                } else {
                    queryCombined = bodyString
                }
            }

            var parametersSigned: MutableMap<String, String> = TreeMap()
            if (queryCombined != null) {
                parametersSigned = queryCombined.queryToParameters().toMutableMap()
            }
            val token = sharedInstance.accessToken ?: getAnonymousToken()
            val appKey = sharedInstance.appKey!!
            val appSecret = sharedInstance.appSecret!!
            val timestamp = Date().time.toString()

            var urlSigned = url
            if (!urlSigned.contains("?")) {
                urlSigned += "?"
            } else if (queryString != null) {
                urlSigned += "&"
            }
            urlSigned += "access_token=" + token.encodeURIComponent()
            urlSigned += "&timestamp=" + timestamp
            urlSigned += "&app_key=" + appKey.encodeURIComponent()

            parametersSigned.put("access_token", token)
            parametersSigned.put("timestamp", timestamp)
            parametersSigned.put("app_key", appKey)
            val sign = md5Signature(parametersSigned, appSecret)
            urlSigned += "&sign=" + sign.encodeURIComponent()
            return urlSigned
        }

        private fun md5Signature(parameters: Map<String, String>, secret: String): String {
            val treeMap = TreeMap(parameters)
            var result = secret
            for (key in treeMap.keys) {
                result += key + treeMap[key]
            }
            result += secret
            result = HexUtil.encodeHexStr(MD5Util.md5(result), false)

            return result
        }

        private val sharedPreferences: SharedPreferences
            get() = Cenarius.context.getSharedPreferences(fileName, 0)

        private val editor: SharedPreferences.Editor
            get() = sharedPreferences.edit()

        /**
         * 获取匿名token
         */
        private fun getAnonymousToken(): String {
                var token = RandomUtil.getRandomNumbersAndLetters(8) + "##ANONYMOUS"
                token = Base64.encodeToString(token.toByteArray(), Base64.NO_WRAP)
                return token
            }
    }

}
