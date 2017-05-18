package com.m.cenarius.Weex;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.alibaba.fastjson.JSONObject;
import com.litesuits.common.io.FileUtils;
import com.m.cenarius.R;

import com.m.cenarius.Route.Route;
import com.m.cenarius.Update.UpdateManager;
import com.orhanobut.logger.Logger;
import com.taobao.weex.IWXRenderListener;
import com.taobao.weex.WXSDKInstance;

import java.io.File;
import java.io.IOException;

public class WeexActivity extends Activity implements IWXRenderListener {

    WXSDKInstance mWXSDKInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_wx);

        mWXSDKInstance = new WXSDKInstance(this);
        mWXSDKInstance.registerRenderListener(this);

        JSONObject params = Route.getParamsJsonObject(this);
        if (params != null) {
            String file = params.getString("file");
            if (file != null) {
                try {
                    File url = UpdateManager.getCacheUrl(file);
                    String template = FileUtils.readFileToString(url);
                    mWXSDKInstance.render(template);
                } catch (IOException e) {
                    Logger.e(e, null);
                }
            }
        }
    }

    @Override
    public void onViewCreated(WXSDKInstance instance, View view) {
        setContentView(view);
    }

    @Override
    public void onRenderSuccess(WXSDKInstance instance, int width, int height) {

    }

    @Override
    public void onRefreshSuccess(WXSDKInstance instance, int width, int height) {

    }

    @Override
    public void onException(WXSDKInstance instance, String errCode, String msg) {

    }

    public void onBackPressed() {
        mWXSDKInstance.fireEvent("_root","androidback");
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mWXSDKInstance!=null){
            mWXSDKInstance.onActivityResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mWXSDKInstance!=null){
            mWXSDKInstance.onActivityPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mWXSDKInstance!=null){
            mWXSDKInstance.onActivityStop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mWXSDKInstance!=null){
            mWXSDKInstance.onActivityDestroy();
        }
    }
}
