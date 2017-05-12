package com.m.cenarius.Route;

import android.content.Context;
import android.content.Intent;

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

        Class toControllerType = sharedInstance.routes.get(UrlUtil.getPath(url));
        if (toControllerType != null) {
            Map<String, String > queryParameters = UrlUtil.getParameters(url);
            JSONObject params = UrlUtil.getParams(url);
            Context fromViewController = from != null ? from : Cenarius.context;
            Intent intent = new Intent(fromViewController, toControllerType);
            intent.putExtra("params", params);
            fromViewController.startActivity(intent);
        }
    }
}
