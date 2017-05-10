package com.m.cenarius.Web.Interceptor;

import android.content.Context;
import android.net.Uri;

import com.m.cenarius.Utils.Utils;

import es.dmoral.toasty.Toasty;

/**
 * Created by m on 2017/5/10.
 */

public class ToastInterceptor implements InterceptorAdapter {

    @Override
    public boolean perform(String url, Context controller) {
        Uri uri = Uri.parse(url);
        if ("cenarius".equals(uri.getScheme()) && "toast".equals(uri.getHost())) {
            Toasty.normal(controller, Utils.getParams(url).getString("text")).show();
            return true;
        }
        return false;
    }
}
