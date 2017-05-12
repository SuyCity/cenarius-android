package com.m.cenarius.Utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.m.cenarius.Native.Cenarius;
import com.orhanobut.logger.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by m on 2017/5/3.
 */

public class Utils {






    public static boolean isRunningForeground(Activity activity) {
        android.app.ActivityManager activityManager = (android.app.ActivityManager) activity.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessInfos = activityManager.getRunningAppProcesses();
        // 枚举进程
        if (appProcessInfos != null && appProcessInfos.size() > 0) {
            for (android.app.ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfos) {
                if (appProcessInfo.importance == android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    if (appProcessInfo.processName.equals(activity.getApplicationInfo().processName)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }





}
