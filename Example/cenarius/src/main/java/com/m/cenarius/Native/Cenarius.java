package com.m.cenarius.Native;

import android.app.Application;
import android.content.Context;

import io.realm.Realm;

/**
 * Created by m on 2017/4/26.
 */

public final class Cenarius {

    public static Context context;

    public static void initialize(Application application) {
        context = application.getApplicationContext();
        Realm.init(context);
    }
}
