package com.purehero.myndkapp00.detect.mirror;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.util.Log;
import android.widget.Toast;

public class MyDisplayManagerCallback implements DisplayManager.DisplayListener {
    private final String TAG = "MyApp00";

    private final Context context;
    private final DisplayManager displayManager;

    public MyDisplayManagerCallback( Context context ) {
        this.context = context;
        this.displayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        this.displayManager.registerDisplayListener( this, null );

        Log.d( TAG, "MyDisplayManagerCallback created" );
    }

    public void release() {
        try {
            displayManager.unregisterDisplayListener(this);
        } catch ( Exception e ) {
        }
        Log.d( TAG, "MyDisplayManagerCallback release" );
    }

    @Override
    public void onDisplayAdded(int i) {
        showToastMessage( "onDisplayAdded( %d )", i );
        // 기본 DisplayID 값은 0 이다.
        // 화면 녹화를 시작하면 0이 아닌 i 값으로 호출된다.
    }

    @Override
    public void onDisplayRemoved(int i) {
        showToastMessage( "onDisplayRemoved( %d )", i );
    }

    @Override
    public void onDisplayChanged(int i) {
        if( i == 0 ) return; // 0 -> 0 으로 변경 이벤트가 들어 오는 경우가 있다. 왜(?) 일까?

        showToastMessage( "onDisplayChanged( %d )", i );
        // 화면 녹화를 종료하면 Added 된 DisplayID 값으로 호출된다.
    }

    private Toast toast = null;
    private void showToastMessage( String format, Object... objs ) {
        String msg = String.format( format, objs );

        if( toast == null ) {
            toast = Toast.makeText( context, msg, Toast.LENGTH_LONG );
        } else {
            toast.cancel();
            toast.setText( msg );
        }
        toast.show();

        Log.d( TAG, msg );
    }
}
