package com.m.cenarius.Utils;

import android.net.Uri;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.orhanobut.logger.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by m on 2017/5/12.
 */

public class UrlUtil {

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

    public static Map<String, String> getParameters(String url) {
        String query = getQuery(url);
        return queryToParameters(query);
    }

    public static Map<String, String> queryToParameters(String query) {
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

    public static String parametersToQuery(Map<String, String> parameters) {
        List<String> pairs = new ArrayList<>();
        for (String key: parameters.keySet()) {
            pairs.add(UrlUtil.encodeURIComponent(key) + "=" + UrlUtil.encodeURIComponent(parameters.get(key)));
        }
        return TextUtils.join("&", pairs);
    }

    public static String getQuery(String url) {
        if (url == null) {
            return null;
        }
        int index = url.indexOf("?");
        if (index > -1) {
            return url.substring(index + 1);
        }
        return null;
    }

    /* 获取parameters里面的params */
    public static JSONObject getParams(String url) {
        if (url == null) {
            return null;
        }
        Map<String, String> queryParameters = getParameters(url);
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
