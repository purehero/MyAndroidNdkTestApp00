package com.purehero.myndkapp00;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.purehero.myndkapp00.databinding.ActivityMainBinding;
import com.purehero.myndkapp00.dialog.AppSealingAlertDialog;

import java.io.IOException;

public class SecondActivity extends AppCompatActivity {
    private static final String TAG = "MyApp00";

    private NativeLibrary nativeLibrary = new NativeLibrary();
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d( TAG, "SecondActivity::onCreate()");

        setContentView(R.layout.activity_second);
    }
}