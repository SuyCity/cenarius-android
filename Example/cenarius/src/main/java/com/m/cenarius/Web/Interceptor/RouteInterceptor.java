package com.m.cenarius.Web.Interceptor;

import android.content.Context;

import com.m.cenarius.Route.Route;
import com.m.cenarius.Utils.UrlUtil;

/**
 * Created by m on 2017/5/10.
 */

public class RouteInterceptor implements InterceptorAdapter {

    @Override
    public boolean perform(String url, Context controller) {
        if ("cenarius".equals(UrlUtil.getScheme(url)) && "route".equals(UrlUtil.getHost(url))) {
            Route.open(url, controller);
            return true;
        }
        return false;
    }
}
