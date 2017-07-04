package com.m.cenarius.Web.Interceptor

import android.app.Activity
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient

import com.alibaba.fastjson.JSONObject
import com.m.cenarius.R
import com.m.cenarius.R2
import com.m.cenarius.Route.Route


import butterknife.BindView
import butterknife.ButterKnife

class WebViewActivity : Activity() {

    @BindView(R2.id.webView)
    internal var webView: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        ButterKnife.bind(this)

        setupWebView()

        webView!!.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return Interceptor.perform(url, webView!!.context)
            }
        })

        val params = Route.getParamsJsonObject(this)
        if (params != null) {
            val url = params.getString("url")
            webView!!.loadUrl(url)
        }
    }

    private fun setupWebView() {
        val webSettings = webView!!.settings
        webSettings.allowContentAccess = true
        webSettings.allowFileAccess = true
        webSettings.allowFileAccessFromFileURLs = true
        webSettings.allowUniversalAccessFromFileURLs = true
        webSettings.setAppCacheEnabled(false)
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false
        webSettings.javaScriptEnabled = true
        webSettings.setGeolocationEnabled(true)
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
    }

    override fun onBackPressed() {
        if (webView!!.canGoBack()) {
            webView!!.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
