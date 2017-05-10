package com.m.cenarius.Web.Interceptor;

import android.content.Context;
import android.net.Uri;

import com.m.cenarius.Route.Route;

/**
 * Created by m on 2017/5/10.
 */

public class RouteInterceptor implements InterceptorAdapter {

    @Override
    public boolean perform(String url, Context controller) {
        Uri uri = Uri.parse(url);
        if ("cenarius".equals(uri.getScheme()) && "route".equals(uri.getHost())) {
            Route.open(url, controller);
            return true;
        }
        return false;
    }
}
