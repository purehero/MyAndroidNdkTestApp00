package com.purehero.myndkapp00.dialog;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class MessageService extends Service implements Runnable {
    private String message = "";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread( this ).start();

        if( intent != null ) {
            if( intent.hasExtra("msg")) {

                Intent dialog_intent = new Intent();
                dialog_intent.setClass( this, MessageDialog.class );
                //intent.setFlags( ServiceInfo.FLAG_STOP_WITH_TASK );
                dialog_intent.setAction( "controller" );
                dialog_intent.putExtra( "msg", "[70007] [Auto Clicker for IDLE Game] 툴이 감지되었습니다. 해당 툴 삭제 후 앱을 다시 실행해 주시기 바랍니다. (설정의 앱관리에서 삭제할 수 있습니다)" );

                startActivity(dialog_intent);
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void run() {
        // Log.d( AppSealingService.TAG_NAME, message );
        try {
            Thread.sleep( 3500 );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        MessageService.this.stopSelf();
    }
}
