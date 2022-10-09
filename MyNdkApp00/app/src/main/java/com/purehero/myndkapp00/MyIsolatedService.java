package com.purehero.myndkapp00;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.system.Os;
import android.util.Log;

public class MyIsolatedService extends Service {
    private static final String TAG = "MyApp00";

    private Messenger messenger = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d( TAG, "MyIsolatedService::onCreate" );
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d( TAG, "MyIsolatedService::onStartCommand" );
        messenger = intent.getParcelableExtra( "messenger" );

        new Thread(new Runnable() {
            @Override
            public void run() {
                responseMessenger();
                MyIsolatedService.this.stopSelf();
            }
        }).start();

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d( TAG, "MyIsolatedService::onBind" );

        messenger = intent.getParcelableExtra( "messenger" );
        return mBinder;
    }

    private final IIsolatedService.Stub mBinder = new IIsolatedService.Stub(){
        public boolean isMagiskPresent(){
            Log.d(TAG, "MyIsolatedService::Isolated UID:"+ Os.getuid());

            responseMessenger();
            return true;
        }
    };

    private void responseMessenger() {
        try {
            if( messenger != null ) {
                Message message = new Message();
                message.what = 100;
                Bundle data = message.getData();
                data.putString("result", "isolatedService ResultMessage");
                message.setData(data);

                messenger.send(message);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
