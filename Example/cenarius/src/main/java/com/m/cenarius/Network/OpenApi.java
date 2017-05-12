package com.m.cenarius.Network;

import android.content.SharedPreferences;

import com.m.cenarius.Native.Cenarius;

import java.util.Map;

/**
 * Created by m on 2017/5/12.
 */

public class OpenApi {

    private static String fileName = "TokenFile";
    private static String accessTokenKey = "CenariusAccessToken";
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
return null;
    }

    private static SharedPreferences getSharedPreferences() {
        return Cenarius.context.getSharedPreferences(fileName, 0);
    }

    private static SharedPreferences.Editor getEditor() {
        return getSharedPreferences().edit();
    }
}
