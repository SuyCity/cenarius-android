package com.m.cenarius.Web.Interceptor;

import android.content.Context;

/**
 * Created by m on 2017/5/10.
 */

public interface InterceptorAdapter {

    boolean perform(String url, Context controller);
}
