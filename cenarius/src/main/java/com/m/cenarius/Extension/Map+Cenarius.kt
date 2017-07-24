package com.m.cenarius.Extension

import com.alibaba.fastjson.JSON
import com.m.cenarius.Network.Parameters
import java.util.ArrayList

/**
 * Created by m on 2017/7/3.
 */

fun Parameters.toQuery(): String {
    val components: MutableList<Pair<String, String>> = ArrayList()
    for (key in this.keys.sorted()) {
        val value = this[key]!!
        components.addAll(Pair(key, value).queryComponents())
    }

    return components.map { "${it.first}=${it.second}" }.joinToString("&")
}

fun Pair<String, Any>.queryComponents(): List<Pair<String, String>> {
    val key = first
    val value = second

    val components: MutableList<Pair<String, String>> = ArrayList()

    val dictionary = value as? Map<String, Any>
    val array = value as? List<Any>
    val bool = value as? Boolean
    if (dictionary != null) {
        for ((nestedKey, value) in dictionary) {
            components.addAll(Pair("$key[$nestedKey]", value).queryComponents())
        }
    } else if (array != null) {
        for (value in array) {
            components.addAll(Pair("$key[]", value).queryComponents())
        }
    } else if (bool != null) {
        components.add(Pair(key.encodeURIComponent(), (if (bool) "1" else "0").encodeURIComponent()))
    } else {
        components.add(Pair(key.encodeURIComponent(), value.toString().encodeURIComponent()))
    }

    return components
}

fun Any.toJSONString(): String {
    return JSON.toJSONString(this)
}