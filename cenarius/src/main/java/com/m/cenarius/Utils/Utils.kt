package com.m.cenarius.Utils

import android.app.Activity
import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.m.cenarius.Native.Cenarius
import com.orhanobut.logger.Logger

import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.ArrayList
import java.util.Collections
import java.util.TreeMap

import android.content.Context.ACTIVITY_SERVICE

/**
 * Created by m on 2017/5/3.
 */

object Utils {


    fun isRunningForeground(activity: Activity): Boolean {
        val activityManager = activity.getSystemService(ACTIVITY_SERVICE) as android.app.ActivityManager
        val appProcessInfos = activityManager.runningAppProcesses
        // 枚举进程
        if (appProcessInfos != null && appProcessInfos.size > 0) {
            for (appProcessInfo in appProcessInfos) {
                if (appProcessInfo.importance == android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    if (appProcessInfo.processName == activity.applicationInfo.processName) {
                        return true
                    }
                }
            }
        }

        return false
    }


}
