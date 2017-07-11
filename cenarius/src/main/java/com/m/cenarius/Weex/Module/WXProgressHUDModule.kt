package com.m.cenarius.Weex.Module

import com.kaopiz.kprogresshud.KProgressHUD
import com.taobao.weex.annotation.JSMethod
import com.taobao.weex.common.WXModule

/**
 * Created by tuotuo on 2017/7/10.
 */
class WXProgressHUDModule : WXModule() {

    val hud = KProgressHUD.create(mWXSDKInstance.context)

    @JSMethod
    fun show() {
        hud.show()
    }

    @JSMethod
    fun dismiss() {
        hud.dismiss()
    }
}