package com.purehero.myndkapp00.test;

import android.content.Context;

public class TestNativeModule {
    static {
        System.loadLibrary("my_test_module01");
    }

    public native void doTest( Context context );
}
