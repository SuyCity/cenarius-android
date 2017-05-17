package com.m.cenarius.Route;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.m.cenarius.Native.Cenarius;
import com.m.cenarius.Utils.UrlUtil;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by m on 2017/5/8.
 */

public class Route {

    private static Route sharedInstance = new Route();
    private Map<String, Class> routes = new TreeMap<>();

    public static void register(String path, Class controller) {
        sharedInstance.routes.put(path, controller);
    }

    public static void open(String url, Context from) {
        open(url, from, null);
    }

    public static void open(String url, Context from , String extraJsonString) {
        Class toControllerType = sharedInstance.routes.get(UrlUtil.getPath(url));
        if (toControllerType != null) {
            Map<String, String > queryParameters = UrlUtil.getParameters(url);
            Context fromViewController = from != null ? from : Cenarius.context;
            Intent intent = new Intent(fromViewController, toControllerType);
            if (extraJsonString != null) {
                intent.putExtra("params", extraJsonString);
            } else {
                String params = UrlUtil.getParamsJsonString(url);
                intent.putExtra("params", params);
            }
            fromViewController.startActivity(intent);
        }
    }

    public static JSONObject getParamsJsonObject(Activity activity) {
        Bundle bundle = activity.getIntent().getExtras();
        return JSON.parseObject(bundle.getString("params"));
    }
}
