package com.m.cenarius.Extension

import android.text.TextUtils
import com.alibaba.fastjson.JSON
import java.util.ArrayList

/**
 * Created by m on 2017/7/3.
 */

fun Map<String, String>.toQuery(): String {
    val pairs = ArrayList<String>()
    for (key in keys) {
        pairs.add(key.encodeURIComponent() + "=" + this[key]!!.encodeURIComponent())
    }
    return TextUtils.join("&", pairs)
}

fun Any.toJSONString(): String {
    return JSON.toJSONString(this)
}