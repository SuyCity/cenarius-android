package com.m.cenarius.activity;

import android.os.Bundle;

import com.m.cenarius.R;
import com.m.cenarius.view.CenariusWebView;
import com.m.cenarius.view.CenariusWidget;


public class CNRSWebViewActivity extends CNRSViewActivity {

    public static final String TAG = CNRSWebViewActivity.class.getSimpleName();

    public CenariusWebView cenariusWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cnrs_web_view_activity);

        cenariusWebView = (CenariusWebView) findViewById(R.id.webView);

        // add widget
        for(CenariusWidget widget: widgets){
            cenariusWebView.addCenariusWidget(widget);
        }
//        htmlFileURL = "https://emcsdev.infinitus.com.cn/h5/www222/build/index.html";
//        htmlFileURL = "file:///android_asset/www/build/index.html";
        String htmlUrl = htmlURL();
        cenariusWebView.loadUrl(htmlUrl);
    }

//    private void loadUrl(String url)
//    {
//        // load uri
//        if (htmlFileURL != null) {
//            mCenariusWebView.loadUrl(htmlFileURL);
//        } else if (uri != null) {
//            mCenariusWebView.loadUri(uri);
//        }
//    }

}
