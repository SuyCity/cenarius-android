package com.m.cenarius.utils;

import org.xutils.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * query 的 map 与 string 互转。注意 map 的 value 是 array
 */

public class QueryUtil {

    public static String mapToString(Map<String, Object> map) {
        if (map == null) {
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (String key : map.keySet()) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append("&");
            }
            Object value = map.get(key);
            try {
                stringBuilder.append((key != null ? URLEncoder.encode(key, "UTF-8") : ""));
                stringBuilder.append("=");
                stringBuilder.append(value != null ? URLEncoder.encode(value.toString(), "UTF-8") : "");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("This method requires UTF-8 encoding support", e);
            }
        }

        return stringBuilder.toString();
    }

    /**
     * 将 query 以字典形式返回。
     */
    public static Map<String, List<String>> queryMap(String query) {
        if (query == null || query.isEmpty()) {
            return null;
        }

        Map<String, List<String>> map = new HashMap<>();

        try {
            // 原 query, 需要 decode
//            query = URLDecoder.decode(query, "UTF-8");
            String[] nameValuePairs = query.split("&");
            for (String nameValuePair : nameValuePairs) {
                String[] nameValue = nameValuePair.split("=");
                String key = URLDecoder.decode(nameValue[0], "UTF-8");
                String value = URLDecoder.decode(nameValuePair.substring(nameValue[0].length() + 1), "UTF-8");
                map = addItemToMap(map, value, key);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return map;
    }

    /**
     * 在字典以关键字添加一个元素。
     *
     * @param item 待添加的元素
     * @param key  关键字
     */
    public static Map<String, List<String>> addItemToMap(Map<String, List<String>> map, String item, String key) {
        List<String> obj = map.get(key);
        List<String> array = new ArrayList<>();
        if (obj != null) {
            array.addAll(obj);
        }
        array.add(item);
        map.put(key, array);
        return map;
    }

    public static String itemForKey(Map<String, List<String>> map, String key) {
        List<String> array = map.get(key);
        if (array == null) {
            return null;
        }
        return array.get(0);
    }

    /**
     * 从 url 中取出 query
     */
    public static String queryFromUrl(String url) {
        if (url == null) {
            return null;
        }
//        return Uri.parse(url).getQuery();
        int index = url.indexOf("?");
        if (index > 0){
            return url.substring(index+1);
        }
        return null;
    }

    /**
     * 从 url 中取出 ? 之前的路径
     */
    public static String baseUrlFromUrl(String url) {
        if (url == null) {
            return null;
        }
        if (url.contains("?")) {
            //如果请求的URL中带有"?",则说明此请求中有带参
            int index = url.indexOf("?");
            return url.substring(0, index);//截取到的URL
        }
        return url;
    }

    public static void addQueryForRequestParams(RequestParams requestParams, String url) {
        String query = queryFromUrl(url);
        Map<String, List<String>> map = queryMap(query);
        if (map != null) {
            for (String key : map.keySet()) {
                requestParams.addQueryStringParameter(key, itemForKey(map, key));
            }
        }
    }
}