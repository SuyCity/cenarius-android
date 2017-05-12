package com.m.example;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;

import com.m.cenarius.Network.OpenApi;
import com.orhanobut.logger.Logger;

import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OpenApiActivity extends Activity {

    @BindView(R.id.urlTextView)
    TextView urlTextView;
    @BindView(R.id.jsonCheckBox)
    CheckBox jsonCheckBox;
    @BindView(R.id.signTextView);
    TextView signTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_api);

        ButterKnife.bind(this);

        OpenApi.setAppKey("APPKEY");
        OpenApi.setAppSecret("APPSECRET");
    }

    @OnClick(R.id.signButton)
    public void sign() {
        String url = (String) urlTextView.getText();
        Logger.d("url: ", url);

        Map<String, String> headers = new TreeMap<>();
        headers.put("X-Requested-With", "OpenAPIRequest");
        if (jsonCheckBox.isChecked()) {
            headers.put("Content-Type", "application/json");
        }

        Map<String, String> parameters = new TreeMap<>();
        parameters.put("pa", "A&A");
        parameters.put("c", "0");

        String urlSign = OpenApi.sign(url, parameters, headers);
        Logger.d("urlSign: ", urlSign);
        signTextView.setText(urlSign);
    }
}
