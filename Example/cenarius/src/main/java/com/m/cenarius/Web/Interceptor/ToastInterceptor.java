package com.m.cenarius.Web.Interceptor;

import android.content.Context;
import android.text.TextUtils;

import com.m.cenarius.Utils.UrlUtil;

import es.dmoral.toasty.Toasty;

/**
 * Created by m on 2017/5/10.
 */

public class ToastInterceptor implements InterceptorAdapter {

    @Override
    public boolean perform(String url, Context controller) {
        if (TextUtils.equals(UrlUtil.getScheme(url), Interceptor.scheme) && TextUtils.equals(UrlUtil.getHost(url), "toast")) {
            Toasty.normal(controller, UrlUtil.getParams(url).getString("text")).show();
            return true;
        }
        return false;
    }
}
