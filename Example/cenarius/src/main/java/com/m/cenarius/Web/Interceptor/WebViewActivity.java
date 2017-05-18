package com.m.cenarius.Web.Interceptor;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alibaba.fastjson.JSONObject;
import com.m.cenarius.R;
import com.m.cenarius.R2;
import com.m.cenarius.Route.Route;
import com.m.cenarius.Update.UpdateManager;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebViewActivity extends Activity {

    @BindView(R2.id.webView)
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        ButterKnife.bind(this);

        setupWebView();

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (!Interceptor.perform(url, webView.getContext())) {
                    view.loadUrl(url);
                }
                return super.shouldOverrideUrlLoading(webView, request);
            }
        });

        JSONObject params = Route.getParamsJsonObject(this);
        if (params != null) {
            String file = params.getString("file");
            String url = params.getString("url");
            if (file != null) {
                File urlFile = UpdateManager.getCacheUrl(file);
                if (urlFile.exists()) {
                    url = "file://" + urlFile.getAbsolutePath();
                    webView.loadUrl(url);
                }
            } else if (url != null) {
                webView.loadUrl(url);
            }
        }
    }

    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setAppCacheEnabled(false);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
    }


}
