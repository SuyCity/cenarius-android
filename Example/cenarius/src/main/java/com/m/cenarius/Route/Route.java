package com.m.cenarius.Route;

import android.app.Activity;
import android.content.Intent;

import com.alibaba.fastjson.JSONObject;
import com.m.cenarius.Utils.Utils;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by m on 2017/5/8.
 */

public class Route {

    private static Route sharedInstance = new Route();
    private Map<String, RouteAdapter> routes = new TreeMap<>();

    public static void register(String path, RouteAdapter controller) {
        sharedInstance.routes.put(path, controller);
    }

    public static void open(String url, Activity from) {
        RouteAdapter toControllerType = sharedInstance.routes.get(url);
        if (toControllerType != null) {
            Map<String, String > queryParameters = Utils.parametersFromUrl(url);
            JSONObject params = Utils.getParams(url);
            Activity toController = toControllerType.instantiate();
            Activity fromViewController = from != null ? from : Utils.topViewController();
            Intent intent = new Intent(fromViewController.this, toController.this);
            fromViewController.startActivity(intent);
        }
    }
}
