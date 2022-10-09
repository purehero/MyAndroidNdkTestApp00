package com.purehero.myndkapp00;

import android.annotation.TargetApi;
import android.app.ZygotePreload;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

@TargetApi(Build.VERSION_CODES.Q)
public class MyZygotePreload implements ZygotePreload {
    private static final String TAG = "MyApp00";

    @Override
    public void doPreload(@NonNull ApplicationInfo applicationInfo) {
        Log.d( TAG, "MyZygotePreload::doPreload" );

        System.loadLibrary("myndkapp01");
    }
}
