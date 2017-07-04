package com.m.cenarius.Extension

import android.net.Uri

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.orhanobut.logger.Logger

import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.ArrayList
import java.util.Collections
import java.util.TreeMap

/**
 * Created by m on 2017/5/12.
 */

fun String.encodeURIComponent(): String {
    try {
        return URLEncoder.encode(this, "UTF-8")
    } catch (e: UnsupportedEncodingException) {
        Logger.e(e, null)
        return ""
    }

}

fun String.decodeURIComponent(): String {
    try {
        return URLDecoder.decode(this, "UTF-8")
    } catch (e: UnsupportedEncodingException) {
        Logger.e(e, null)
        return ""
    }
}

fun String.getParameters(): Map<String, String> {
    val query = getQuery()
    return query.queryToParameters()
}

fun String?.queryToParameters(): Map<String, String> {
    val results = TreeMap<String, String>()
    if (this == null) {
        return results
    }
    val parametersCombined = TreeMap<String, ArrayList<String>>()
    val pairs = this.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    for (pair in pairs) {
        val keyValue = pair.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val key = keyValue[0].decodeURIComponent()
        val value = keyValue[1].decodeURIComponent()
        if (parametersCombined[key] != null) {
            parametersCombined[key]!!.add(value)
        } else {
            val values = ArrayList<String>()
            values.add(value)
            parametersCombined.put(key, values)
        }
    }
    for (key in parametersCombined.keys) {
        val values = parametersCombined[key]
        Collections.sort(values)
        var value = values!!.get(0)
        for (i in 1..values!!.size - 1) {
            value = value + key + values.get(i)
        }
        results.put(key, value)
    }
    return results
}



fun String?.getQuery(): String? {
    if (this == null) {
        return null
    }
    val index = this.indexOf("?")
    if (index > -1) {
        return this.substring(index + 1)
    }
    return null
}

/* 获取parameters里面的params */
fun String.getParamsJsonObject(): JSONObject {
    return JSON.parseObject(getParamsJsonString())
}

fun String?.getParamsJsonString(): String? {
    if (this == null) {
        return null
    }
    val queryParameters = getParameters()
    return queryParameters["params"]
}

fun String.getPath(): String {
    val uri = Uri.parse(this)
    return uri.path
}

fun String.getScheme(): String {
    val uri = Uri.parse(this)
    return uri.scheme
}

fun String.getHost(): String {
    val uri = Uri.parse(this)
    return uri.host
}
