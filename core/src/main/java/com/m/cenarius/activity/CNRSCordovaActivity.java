package com.m.cenarius.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.apache.cordova.CordovaActivity;

public class CNRSCordovaActivity extends CordovaActivity {

    public View progress;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.init();

        Log.v("cenarius", "loadUri , uri = " + (null != uri ? uri : "null"));

        String htmlUrl = htmlURL();
        if (htmlUrl != null) {
            loadUrl(htmlUrl);
        } else {
            Log.v("cenarius", "htmlUrl 为空");
        }
    }


}
