package com.m.cenarius.Extension

import android.text.TextUtils
import java.util.ArrayList

/**
 * Created by m on 2017/7/3.
 */

fun Map<String, String>.toQuery(): String {
    val pairs = ArrayList<String>()
    for (key in this.keys) {
        pairs.add(key.encodeURIComponent() + "=" + this[key]!!.encodeURIComponent())
    }
    return TextUtils.join("&", pairs)
}