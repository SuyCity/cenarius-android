package com.m.example;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

import com.m.cenarius.Update.UpdateManager;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity {

    @BindView(R.id.update)
    Button update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        update.getText();
    }

    @OnClick(R.id.update)
    public void update() {
        UpdateManager.setDevelopMode(false);
        UpdateManager.setServerUrl("http://172.20.70.80/www");
        UpdateManager.update(new UpdateManager.UpdateCallback() {
            @Override
            public void completion(UpdateManager.State state, int progress) {
                Logger.d(state);
                Logger.d(progress);

            }
        });
    }
}
