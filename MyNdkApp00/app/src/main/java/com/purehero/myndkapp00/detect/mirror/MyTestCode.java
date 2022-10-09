package com.purehero.myndkapp00.detect.mirror;

import android.os.IBinder;
import android.util.Log;

public class MyTestCode {
    private static final String TAG = "MyApp00";

    public MyTestCode() {
    }

    public void doTest() {
        IBinder display = SurfaceControl.createDisplay( "purehero", true );
        long Ids [] = SurfaceControl.getPhysicalDisplayIds();
        if( Ids.length > 0 ) {
            for( long id : Ids ) {
                Log.d( TAG, String.format( "display id : %ld", id ));
            }
        }
    }
}
