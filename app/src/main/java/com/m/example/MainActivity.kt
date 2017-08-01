package com.m.example

import android.app.Activity
import android.content.Intent
import android.os.Bundle

import com.kaopiz.kprogresshud.KProgressHUD
import com.m.cenarius.Extension.*
import com.m.cenarius.Native.Cenarius
import com.m.cenarius.Route.Route
import com.m.cenarius.Update.UpdateManager
import com.orhanobut.logger.Logger
import java.util.TreeMap

import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {

    private lateinit var hud: KProgressHUD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        update.setOnClickListener { update() }
        openApi.setOnClickListener { sign() }
        weex.setOnClickListener { weex() }
        webView.setOnClickListener { webView() }
        location.setOnClickListener { locationBtn() }
    }

    private fun locationBtn() {
        startActivity(Intent(this, LocationActivity::class.java))
    }

    fun update() {
        hud = KProgressHUD.create(this).setStyle(KProgressHUD.Style.BAR_DETERMINATE).setLabel("Update").setMaxProgress(100).setCancellable(false).show()
        UpdateManager.update(object : UpdateManager.UpdateCallback {
            override fun completion(state: UpdateManager.State, progress: Int) {
                Logger.d(state)
                Logger.d(progress)
                when (state) {
                    UpdateManager.State.UNZIP_WWW -> {
                        hud.setLabel("unzip")
                        hud.setProgress(progress)
                    }
                    UpdateManager.State.DOWNLOAD_FILES -> {
                        hud.setLabel("download")
                        hud.setProgress(progress)
                    }
                    UpdateManager.State.UPDATE_SUCCESS -> {
                        hud.dismiss()
                        Toasty.normal(Cenarius.context, "success").show()
                    }
                    else -> {
                        hud.dismiss()
                        Toasty.error(Cenarius.context, "error").show()
                    }
                }
            }
        })
    }

    fun sign() {
        Route.open("cenarius://route/sign", this)
    }

    fun weex() {
        val file = "weex/index.js"
        val params = TreeMap<String, String>()
        params.put("file", file)
        Route.open("/weex", params.toJSONString(), this)
    }

    fun webView() {
        val url = UpdateManager.serverUrl + "/vux/index.html"
        val params = TreeMap<String, String>()
        params.put("url", url)
        Route.open("/webView", params.toJSONString() ,this)
    }

}
