package com.m.cenarius.Extension

import android.app.Activity
import android.content.Context
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject

/**
 * Created by m on 2017/7/4.
 */

fun Activity.getParamsJsonObject(): JSONObject? {
    val bundle = this.intent.extras
    return JSON.parseObject(bundle.getString("params"))
}

fun Activity.isRunningForeground(): Boolean {
    val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
    val appProcessInfos = activityManager.runningAppProcesses
    // 枚举进程
    if (appProcessInfos != null && appProcessInfos.size > 0) {
        for (appProcessInfo in appProcessInfos) {
            if (appProcessInfo.importance == android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                if (appProcessInfo.processName == applicationInfo.processName) {
                    return true
                }
            }
        }
    }

    return false
}