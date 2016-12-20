package com.m.cenarius.utils;

import android.util.Base64;

import com.m.cenarius.Cenarius;
import com.m.cenarius.widget.LoginWidget;

import org.xutils.common.util.KeyValue;
import org.xutils.http.RequestParams;

import java.security.MessageDigest;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class OpenApi {

    /**
     * md5 签名
     */
    public static String md5Signature(Map<String, String> params, String secret) {
        String result;
        StringBuffer orgin = getBeforeSign(params, new StringBuffer(secret));
        if (orgin == null) {
            return null;
        }
        // secret last
        orgin.append(secret);
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            result = byte2hex(md.digest(orgin.toString().getBytes("utf-8")));
        } catch (Exception e) {
            throw new java.lang.RuntimeException("md5 sign error !", e);
        }
        return result;
    }

    private static StringBuffer getBeforeSign(Map<String, String> params, StringBuffer orgin) {
        if (params == null) {
            return null;
        }

        Map<String, String> map = new TreeMap<>();
        map.putAll(params);
        for (String key : map.keySet()) {
            orgin.append(key).append(params.get(key));
        }
        return orgin;
    }

    private static String byte2hex(byte[] b) {
        StringBuffer hs = new StringBuffer();
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs.append("0").append(stmp);
            } else {
                hs.append(stmp);
            }
        }
        return hs.toString().toUpperCase();
    }

//    /**
//     * 返回签名之后的 Query
//     */
//    public static String openApiQuery(String query, String body) {
//
//        // 系统级参数
//        String token = LoginWidget.getAccessToken();
//        String appKey = Cenarius.LoginAppKey;
//        String appSecret = Cenarius.LoginAppSecret;
//        String timestamp = Long.toString((new Date()).getTime());
//        if (token == null || appKey == null || appSecret == null) {
//            return null;
//        }
//
//        String parameterString = query;
//        if (body != null) {
//            if (parameterString != null) {
//                parameterString = parameterString + "&" + body;
//            } else {
//                parameterString = body;
//            }
//        }
//        // 多值合并
//        Map<String, String> parameters = new HashMap<>();
//        Map<String, List<String>> oldParameters = QueryUtil.queryMap(parameterString);
//        if (oldParameters != null) {
//            for (String key : oldParameters.keySet()) {
//                List<String> array = oldParameters.get(key);
//                Collections.sort(array);
//                String value = array.get(0);
//                for (int i = 1; i < array.size(); i++) {
//                    value = value + key + array.get(i);
//                }
//                parameters.put(key, value);
//            }
//        }
//
//        parameters.put("access_token", token);
//        parameters.put("app_key", appKey);
//        parameters.put("timestamp", timestamp);
//
//        // 签名
//        String sign = md5Signature(parameters, appSecret);
//        if (query != null && query.length() > 0) {
//            query = query + "&access_token=" + token + "&app_key=" + appKey + "&timestamp=" + timestamp + "&sign=" + sign;
//        } else {
//            query = "access_token=" + token + "&app_key=" + appKey + "&timestamp=" + timestamp + "&sign=" + sign;
//        }
//
//        return query;
//    }

    public static void openApiForRequestParams(RequestParams requestParams) {

        List<KeyValue> queryStringParams = requestParams.getQueryStringParams();
        List<KeyValue> bodyParams = requestParams.getBodyParams();
        if (queryStringParams != null) {
            for (KeyValue keyValue : queryStringParams) {
                if ("sign".equals(keyValue.key)) {
                    // 已经有签名，不需要处理
                    return;
                }
            }
        }

        String query = getKeyValueString(queryStringParams);
        String body = getKeyValueString(bodyParams);

        String parameterString = query;
        if (body != null) {
            if (parameterString != null) {
                parameterString = parameterString + "&" + body;
            } else {
                parameterString = body;
            }
        }

        // 多值合并
        Map<String, String> parameters = new HashMap<>();
        Map<String, List<String>> oldParameters = QueryUtil.queryMap(parameterString);
        if (oldParameters != null) {
            for (String key : oldParameters.keySet()) {
                List<String> array = oldParameters.get(key);
                Collections.sort(array);
                String value = array.get(0);
                for (int i = 1; i < array.size(); i++) {
                    value = value + key + array.get(i);
                }
                parameters.put(key, value);
            }
        }

        // 加入系统级参数
        String token = LoginWidget.getAccessToken();
        String appKey = Cenarius.LoginAppKey;
        String appSecret = Cenarius.LoginAppSecret;
        String timestamp = Long.toString((new Date()).getTime());
        if (token == null) {
            token = getAnonymousToken();
        }
        parameters.put("access_token", token);
        parameters.put("app_key", appKey);
        parameters.put("timestamp", timestamp);

        // 签名
        String sign = md5Signature(parameters, appSecret);

        requestParams.addQueryStringParameter("access_token", token);
        requestParams.addQueryStringParameter("app_key", appKey);
        requestParams.addQueryStringParameter("timestamp", timestamp);
        requestParams.addQueryStringParameter("sign", sign);
    }

    private static String getKeyValueString(List<KeyValue> list) {
        String query = null;
        if (list != null) {
            for (KeyValue keyValue : list) {
                if (query == null) {
                    query = keyValue.key + "=" + keyValue.value;
                } else {
                    query = query + "&" + keyValue.key + "=" + keyValue.value;
                }
            }
        }
        return query;
    }

    private static String getAnonymousToken() {
        String token = "ANONYMOUS##" + createRandom(false, 8);
        token = Base64.encodeToString(token.getBytes(), Base64.DEFAULT);
        return token;
    }

    /**
     * 创建指定数量的随机字符串
     *
     * @param numberFlag 是否是数字
     * @param length
     * @return
     */
    private static String createRandom(boolean numberFlag, int length) {
        String retStr = "";
        String strTable = numberFlag ? "1234567890" : "1234567890abcdefghijkmnpqrstuvwxyz";
        int len = strTable.length();
        boolean bDone = true;
        do {
            retStr = "";
            int count = 0;
            for (int i = 0; i < length; i++) {
                double dblR = Math.random() * len;
                int intR = (int) Math.floor(dblR);
                char c = strTable.charAt(intR);
                if (('0' <= c) && (c <= '9')) {
                    count++;
                }
                retStr += strTable.charAt(intR);
            }
            if (count >= 2) {
                bDone = false;
            }
        } while (bDone);

        return retStr;
    }
}
