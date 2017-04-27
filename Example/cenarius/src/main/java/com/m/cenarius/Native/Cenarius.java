package com.m.cenarius.Native;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by m on 2017/4/26.
 */

public final class Cenarius {

    public static Application application;

    public static void initialize(Application application) {
        Cenarius.application = application;
        Realm.init(application);
    }
}
