package com.m.example;

import android.app.Activity;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.m.cenarius.Native.Cenarius;
import com.m.cenarius.Route.Route;
import com.m.cenarius.Update.UpdateManager;
import com.orhanobut.logger.Logger;

import java.util.Map;
import java.util.TreeMap;

import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

public class MainActivity extends Activity {

    private KProgressHUD hud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.update)
    public void update() {
        hud = KProgressHUD.create(this).setStyle(KProgressHUD.Style.BAR_DETERMINATE).setLabel("Update").setMaxProgress(100).setCancellable(false).show();
        UpdateManager.update(new UpdateManager.UpdateCallback() {
            @Override
            public void completion(UpdateManager.State state, int progress) {
                Logger.d(state);
                Logger.d(progress);
                if (state == UpdateManager.State.UNZIP_WWW) {
                    hud.setLabel("unzip");
                    hud.setProgress(progress);
                } else if (state == UpdateManager.State.DOWNLOAD_FILES) {
                    hud.setLabel("download");
                    hud.setProgress(progress);
                } else if (state == UpdateManager.State.UPDATE_SUCCESS) {
                    hud.dismiss();
                    Toasty.normal(Cenarius.context, "success").show();
                } else if (state == UpdateManager.State.DOWNLOAD_CONFIG_FILE_ERROR || state == UpdateManager.State.DOWNLOAD_FILES_ERROR || state == UpdateManager.State.DOWNLOAD_FILES_FILE_ERROR || state == UpdateManager.State.UNZIP_WWW_ERROR) {
                    hud.dismiss();
                }
            }
        });
    }

    @OnClick(R.id.openApi)
    public void sign() {
        Route.open("cenarius://route/sign", this);
    }

    @OnClick(R.id.weex)
    public void weex() {
        String file = "weex/index.js";
        Map<String, String> params = new TreeMap<>();
        params.put("file", file);
        Route.open("cenarius://route/weex", this, JSON.toJSONString(params));
    }

    @OnClick(R.id.webView)
    public void webView() {
        String url = UpdateManager.getServerUrl() + "/vux/index.html";
        Map<String, String> params = new TreeMap<>();
        params.put("url", url);
        Route.open("cenarius://route/webView", this, JSON.toJSONString(params));
    }

}
