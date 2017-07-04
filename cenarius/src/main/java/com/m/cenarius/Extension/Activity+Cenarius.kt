package com.m.cenarius.Extension

import android.app.Activity
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject

/**
 * Created by m on 2017/7/4.
 */

fun Activity.getParamsJsonObject(): JSONObject {
    val bundle = this.intent.extras
    return JSON.parseObject(bundle.getString("params"))
}