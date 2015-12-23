package com.ls.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by user on 15-12-17.
 */
public class BaseActivity extends AppCompatActivity {

    private boolean mFirstStart = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mFirstStart) {
            mFirstStart = false;
            initActionBar();
        }
    }

    protected void initActionBar(){}
}
