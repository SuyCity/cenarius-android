package com.m.cenarius.Web.Interceptor;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by m on 2017/5/10.
 */

public class Interceptor {

    public static final String scheme = "cenarius";

    private List<InterceptorAdapter> interceptors = new ArrayList<>();
    private static Interceptor sharedInstance = new Interceptor();

    public static void register(InterceptorAdapter interceptor) {
        sharedInstance.interceptors.add(interceptor);
    }

    public static boolean perform(String url, Context controller) {
        for (InterceptorAdapter interceptor: sharedInstance.interceptors) {
            if (interceptor.perform(url, controller)) {
                return true;
            }
        }
        return false;
    }
}
