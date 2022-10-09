package com.purehero.myndkapp00;

public class NativeLibrary {
    // Used to load the 'myndkapp00' library on application startup.
    static {
        System.loadLibrary("myndkapp00");
    }

    /**
     * A native method that is implemented by the 'myndkapp00' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
    public native void killMyProcess( int sec );
}
