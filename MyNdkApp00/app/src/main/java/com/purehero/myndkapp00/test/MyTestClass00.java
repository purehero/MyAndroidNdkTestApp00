package com.purehero.myndkapp00.test;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.util.List;

public class MyTestClass00 {
    /*
    if (Build.VERSION.SDK_INT >= 33) {
        applicationInfos = a.getPackageManager().getInstalledApplications( android.content.pm.PackageManager.ApplicationInfoFlags.of(PackageManager.GET_META_DATA));
    } else {
        packageInfos = a.getPackageManager().getInstalledApplications( PackageManager.GET_META_DATA );
    }
    */

    private static final String TAG = "MyApp00";

    private final Context context;
    public MyTestClass00( Context context ) {
        this.context = context;
    }

    TestNativeModule testModule = new TestNativeModule();

    public void RunTest() {
        PackageManager pm = context.getPackageManager();

        List<ApplicationInfo> infos = null;
        if(Build.VERSION.SDK_INT >= 33 ) {
            infos = pm.getInstalledApplications(PackageManager.ApplicationInfoFlags.of(PackageManager.GET_META_DATA));
        } else {
            infos = pm.getInstalledApplications(PackageManager.GET_META_DATA );
        }

        Log.d( TAG, "Application infos ==============================");
        for( ApplicationInfo info : infos ) {
            Log.d( TAG, info.toString());
        }
        Log.d( TAG, "================================================");

        testModule.doTest( context );
    }
}
