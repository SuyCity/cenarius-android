package com.m.cenarius.Native

import android.app.Application
import android.content.Context
import io.realm.Realm

/**
 * Created by m on 2017/7/3.
 */
open class Cenarius {

    companion object {

        lateinit var context: Context

        fun initialize(application: Application) {
            context = application.applicationContext
            Realm.init(context)
        }
    }
}