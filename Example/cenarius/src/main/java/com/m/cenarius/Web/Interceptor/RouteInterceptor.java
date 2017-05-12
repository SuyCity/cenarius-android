package com.m.cenarius.Web.Interceptor;

import android.content.Context;
import android.text.TextUtils;

import com.m.cenarius.Route.Route;
import com.m.cenarius.Utils.UrlUtil;

/**
 * Created by m on 2017/5/10.
 */

public class RouteInterceptor implements InterceptorAdapter {

    @Override
    public boolean perform(String url, Context controller) {
        if (TextUtils.equals(UrlUtil.getScheme(url), "cenarius") && TextUtils.equals(UrlUtil.getHost(url), "route")) {
            Route.open(url, controller);
            return true;
        }
        return false;
    }
}
