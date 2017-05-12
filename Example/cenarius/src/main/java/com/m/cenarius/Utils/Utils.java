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

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean hasJellyBeanMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    public static boolean hasKitkat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean hasM() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getAppVersionName() {
        PackageManager manager = Cenarius.context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(Cenarius.context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


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

    /**
     * 比较版本号的大小,前者大则返回一个正数,后者大返回一个负数,相等则返回0
     *
     * @param version1
     * @param version2
     * @return
     */
    public static int compareVersion(String version1, String version2) {
        if (version1 == null) {
            version1 = "0";
        }
        if (version2 == null) {
            version2 = "0";
        }
        String[] versionArray1 = version1.split("\\.");//注意此处为正则匹配，不能用"."；
        String[] versionArray2 = version2.split("\\.");
        int idx = 0;
        int minLength = Math.min(versionArray1.length, versionArray2.length);//取最小长度值
        int diff = 0;
        while (idx < minLength
                && (diff = versionArray1[idx].length() - versionArray2[idx].length()) == 0//先比较长度
                && (diff = versionArray1[idx].compareTo(versionArray2[idx])) == 0) {//再比较字符
            ++idx;
        }
        //如果已经分出大小，则直接返回，如果未分出大小，则再比较位数，有子版本的为大；
        diff = (diff != 0) ? diff : versionArray1.length - versionArray2.length;
        return diff;
    }

    public static String encodeURIComponent(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Logger.e(e, null);
            return null;
        }
    }

    public static String decodeURIComponent(String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Logger.e(e, null);
            return null;
        }
    }

    public static Map<String, String> parametersFromUrl(String url) {
        String query = queryFromUrl(url);
        return parametersFromQuery(query);
    }

    public static Map<String, String> parametersFromQuery(String query) {
        Map<String, String> results = new TreeMap<>();
        if (query == null) {
            return results;
        }
        Map<String, List<String>> parametersCombined = new TreeMap<>();
        String[] pairs = query.split("&");
        for (String pair: pairs) {
            String[] keyValue = pair.split("=");
            String key = decodeURIComponent(keyValue[0]);
            String value = decodeURIComponent(keyValue[1]);
            if (parametersCombined.get(key) != null) {
                parametersCombined.get(key).add(value);
            } else {
                List<String> values = new ArrayList<>();
                values.add(value);
                parametersCombined.put(key, values);
            }
        }
        for (String key: parametersCombined.keySet()) {
            List<String> values = parametersCombined.get(key);
            Collections.sort(values);
            String value = values.get(0);
            for (int i = 1; i < values.size(); i++) {
                value = value + key + values.get(i);
            }
            results.put(key, value);
        }
        return results;
    }

    public static String queryFromUrl(String url) {
        if (url == null) {
            return null;
        }
        int index = url.indexOf("?");
        if (index > -1) {
            return url.substring(index + 1);
        }
        return null;
    }

    public static JSONObject getParams(String url) {
        if (url == null) {
            return null;
        }
        Map<String, String> queryParameters = parametersFromUrl(url);
        JSONObject params = null;
        String paramsString = queryParameters.get("params");
        if (paramsString != null) {
            params = JSON.parseObject(paramsString);
        }
        return params;
    }

    public static String getPath(String url) {
        Uri uri = Uri.parse(url);
        return uri.getPath();
    }

    public static String getScheme(String url) {
        Uri uri = Uri.parse(url);
        return uri.getScheme();
    }

    public static String getHost(String url) {
        Uri uri = Uri.parse(url);
        return uri.getHost();
    }

}
