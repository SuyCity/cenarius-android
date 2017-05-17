package com.m.example;

import android.app.Activity;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.m.cenarius.Route.Route;
import com.m.cenarius.Update.UpdateManager;
import com.orhanobut.logger.Logger;

import java.util.Map;
import java.util.TreeMap;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.update)
    public void update() {
        UpdateManager.setDevelopMode(false);
        UpdateManager.setServerUrl("http://172.20.70.80/www");
        UpdateManager.update(new UpdateManager.UpdateCallback() {
            @Override
            public void completion(UpdateManager.State state, int progress) {
                Logger.d(state);
                Logger.d(progress);

            }
        });
    }

    @OnClick(R.id.openApi)
    public void sign() {
        Route.open("cenarius://route/sign", this);
    }

    @OnClick(R.id.weex)
    public void weex() {
        String url = "weex/index.js";
        Map<String, String> params = new TreeMap<>();
        params.put("url", url);
        Route.open("cenarius://route/weex", this, JSON.toJSONString(params));
    }

}
