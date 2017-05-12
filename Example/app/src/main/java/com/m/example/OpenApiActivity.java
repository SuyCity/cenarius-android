package com.m.example;

import android.app.Activity;
import android.os.Bundle;

import com.m.cenarius.Network.OpenApi;

public class OpenApiActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_api);

        OpenApi.setAppKey("APPKEY");
        OpenApi.setAppSecret("APPSECRET");
    }
}
