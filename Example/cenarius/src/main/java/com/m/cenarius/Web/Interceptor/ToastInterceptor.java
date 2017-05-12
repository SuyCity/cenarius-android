package com.m.cenarius.Web.Interceptor;

import android.content.Context;

import com.m.cenarius.Utils.UrlUtil;

import es.dmoral.toasty.Toasty;

/**
 * Created by m on 2017/5/10.
 */

public class ToastInterceptor implements InterceptorAdapter {

    @Override
    public boolean perform(String url, Context controller) {
        if ("cenarius".equals(UrlUtil.getScheme(url)) && "toast".equals(UrlUtil.getHost(url))) {
            Toasty.normal(controller, UrlUtil.getParams(url).getString("text")).show();
            return true;
        }
        return false;
    }
}
