package com.m.cenarius.Web.Interceptor;

import android.content.Context;

import com.m.cenarius.Route.Route;
import com.m.cenarius.Utils.Utils;

/**
 * Created by m on 2017/5/10.
 */

public class RouteInterceptor implements InterceptorAdapter {

    @Override
    public boolean perform(String url, Context controller) {
        if ("cenarius".equals(Utils.getScheme(url)) && "route".equals(Utils.getHost(url))) {
            Route.open(url, controller);
            return true;
        }
        return false;
    }
}
