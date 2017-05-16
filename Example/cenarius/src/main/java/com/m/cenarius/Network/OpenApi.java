package com.m.cenarius.Network;

import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

import com.alibaba.fastjson.JSON;
import com.litesuits.common.utils.HexUtil;
import com.litesuits.common.utils.MD5Util;
import com.litesuits.common.utils.RandomUtil;
import com.m.cenarius.Native.Cenarius;
import com.m.cenarius.Utils.UrlUtil;


import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by m on 2017/5/12.
 */

public class OpenApi {

    public static final String xRequestKey = "X-Requested-With";
    public static final String xRequestValue = "OpenAPIRequest";
    public static final String contentTypeKey = "Content-Type";
    public static final String contentTypeValue = "application/json";

    private static final String fileName = "TokenFile";
    private static final String accessTokenKey = "CenariusAccessToken";
    private static OpenApi sharedInstance = new OpenApi();
    private String accessToken = getSharedPreferences().getString(accessTokenKey, null);
    private String appKey;
    private String appSecret;

    public static void setAccessToken(String token) {
        sharedInstance.accessToken = token;
        getEditor().putString(accessTokenKey, token).commit();
    }

    public static String getAccessToken() {
        return sharedInstance.accessToken;
    }

    public static void setAppKey(String key) {
        sharedInstance.appKey = key;
    }

    public static void setAppSecret(String secret) {
        sharedInstance.appSecret = secret;
    }

    public static String sign(String url, Map<String, String> parameters, Map<String, String> headers) {
        boolean isOpenApi = false;
        boolean isJson= false;
        if (headers != null) {
            for (String key: headers.keySet()) {
                if (TextUtils.equals(key, xRequestKey) && TextUtils.equals(headers.get(key), xRequestValue)) {
                    isOpenApi = true;
                }
                if (TextUtils.equals(key, contentTypeKey) && headers.get(key).contains(contentTypeValue)) {
                    isJson = true;
                }
            }
        }

        if (!isOpenApi) {
            return url;
        }

        String querySting = UrlUtil.getQuery(url);
        String queryCombined = querySting;
        String bodySting;
        if (!parameters.isEmpty()) {
            if (isJson) {
                bodySting = "openApiBodyString=" + UrlUtil.encodeURIComponent(JSON.toJSONString(parameters));
            } else {
                bodySting = UrlUtil.parametersToQuery(parameters);
            }
            if (queryCombined != null) {
                queryCombined += "&" + bodySting;
            } else {
                queryCombined = bodySting;
            }
        }

        Map<String, String> parametersSigned = new TreeMap<>();
        if (queryCombined != null) {
            parametersSigned = UrlUtil.queryToParameters(queryCombined);
        }
        String token = sharedInstance.accessToken != null ? sharedInstance.accessToken : getAnonymousToken();
        String appKey = sharedInstance.appKey;
        String appSecret = sharedInstance.appSecret;
        String timestamp = Long.toString((new Date()).getTime());

        String urlSigned = url;
        if (!urlSigned.contains("?")) {
            urlSigned += "?";
        } else if (querySting != null) {
            urlSigned += "&";
        }
        urlSigned += "access_token=" + UrlUtil.encodeURIComponent(token);
        urlSigned += "&timestamp=" + timestamp;
        urlSigned += "&app_key=" + UrlUtil.encodeURIComponent(appKey);

        parametersSigned.put("access_token", token);
        parametersSigned.put("timestamp", timestamp);
        parametersSigned.put("app_key", appKey);
        String sign = md5Signature(parametersSigned, appSecret);
        urlSigned += "&sign=" + UrlUtil.encodeURIComponent(sign);
        return urlSigned;
    }

    private static String md5Signature(Map<String, String> parameters, String secret) {
        Map<String, String> treeMap = new TreeMap<>(parameters);
        String result = secret;
        for (String key: treeMap.keySet()) {
            result += key + treeMap.get(key);
        }
        result += secret;
        result = HexUtil.encodeHexStr(MD5Util.md5(result), false);

        return result;
    }

    private static SharedPreferences getSharedPreferences() {
        return Cenarius.context.getSharedPreferences(fileName, 0);
    }

    private static SharedPreferences.Editor getEditor() {
        return getSharedPreferences().edit();
    }

    /**
     * 获取匿名token
     */
    private static String getAnonymousToken() {
        String token = RandomUtil.getRandomNumbersAndLetters(8) + "##ANONYMOUS";
        token = Base64.encodeToString(token.getBytes(), Base64.NO_WRAP);
        return token;
    }

}
