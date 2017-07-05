package com.m.cenarius.Weex

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.Window

import com.litesuits.common.io.FileUtils
import com.m.cenarius.Extension.*
import com.m.cenarius.R

import com.m.cenarius.Update.UpdateManager
import com.orhanobut.logger.Logger
import com.taobao.weex.IWXRenderListener
import com.taobao.weex.WXSDKInstance

import java.io.IOException

open class WeexActivity : Activity(), IWXRenderListener {

    open var mWXSDKInstance: WXSDKInstance? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_weex)

        mWXSDKInstance = WXSDKInstance(this)
        mWXSDKInstance!!.registerRenderListener(this)

        val params = getParamsJsonObject()
        if (params != null) {
            val file = params.getString("file")
            if (file != null) {
                try {
                    val url = UpdateManager.getCacheUrl(file)
                    val template = FileUtils.readFileToString(url)
                    mWXSDKInstance!!.render(template)
                } catch (e: IOException) {
                    Logger.e(e, null)
                }

            }
        }
    }

    override fun onViewCreated(instance: WXSDKInstance, view: View) {
        setContentView(view)
    }

    override fun onRenderSuccess(instance: WXSDKInstance, width: Int, height: Int) {

    }

    override fun onRefreshSuccess(instance: WXSDKInstance, width: Int, height: Int) {

    }

    override fun onException(instance: WXSDKInstance, errCode: String, msg: String) {

    }

    override fun onBackPressed() {
        mWXSDKInstance?.fireEvent("_root", "androidback")
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        mWXSDKInstance?.onActivityResume()
    }

    override fun onPause() {
        super.onPause()
        mWXSDKInstance?.onActivityPause()
    }

    override fun onStop() {
        super.onStop()
        mWXSDKInstance?.onActivityStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mWXSDKInstance?.onActivityDestroy()
    }
}
