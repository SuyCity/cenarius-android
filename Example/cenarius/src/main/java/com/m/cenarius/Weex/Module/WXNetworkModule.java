package com.m.cenarius.Weex.Module;

import android.text.TextUtils;

import com.m.cenarius.Network.Network;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.common.WXModule;

import java.util.Map;

/**
 * Created by m on 2017/6/28.
 */

public class WXNetworkModule extends WXModule {

    @JSMethod
    public void request(Map<String, Object> options, String callBackId) {
        String url = (String)options.get("url");
        Network.HTTPMethod method = Network.HTTPMethod.GET;
        String m = (String)options.get("method");
        if (TextUtils.equals(m, "POST")) {
            method = Network.HTTPMethod.POST;
        }



//        Network.requset()
        WXSDKEngine.callback(mWXSDKInstance.getInstanceId(), callBackId, options);
    }
}
