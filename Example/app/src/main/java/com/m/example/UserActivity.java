package com.m.example;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.m.cenarius.Route.Route;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserActivity extends Activity {

    @BindView(R.id.idTextView)
    TextView idTextView;
    @BindView(R.id.nameTextView)
    TextView nameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        ButterKnife.bind(this);

        JSONObject params = Route.getParamsJsonObject(this);
        if (params != null) {
            String id = params.getString("id");
            String name = params.getString("name");
            idTextView.setText(id);
            nameTextView.setText(name);
        }
    }
}
