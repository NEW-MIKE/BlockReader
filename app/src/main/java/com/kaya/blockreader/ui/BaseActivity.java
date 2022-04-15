package com.kaya.blockreader.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kaya.blockreader.blockApplication;
import com.kaya.blockreader.utils.DisplayUtil;


public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayUtil.setCustomDensity(this, blockApplication.getInstance());
    }
}
