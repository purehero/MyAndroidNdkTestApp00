package com.purehero.myndkapp00;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.purehero.myndkapp00.detect.mirror.MyDisplayManagerCallback;
import com.purehero.myndkapp00.detect.mirror.MyMediaRouterCallback;
import com.purehero.myndkapp00.detect.mirror.MyTestCode;
import com.purehero.myndkapp00.test.MyTestClass00;

public class MainApplication extends Application {
    public static final String TAG = "MyApp00";
    private MyMediaRouterCallback myMediaRouterCallback = null;
    private MyDisplayManagerCallback myDisplayManagerCallback = null;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        Log.d( TAG, "MainApplication::OnAttachBaseContext" );
    }

    @Override
    public void onCreate() {
        super.onCreate();

        boolean bIsMainProcess = isMainProcess( this );
        Log.d( TAG, String.format( "MainApplication::OnCreate => %s Process", bIsMainProcess ? "Main" : "NotMain" ));

        //new MyTestClass00( this ).RunTest();
        //new MyTestCode().doTest();
/*
    if( bIsMainProcess ) {
            myMediaRouterCallback = new MyMediaRouterCallback(this);
            myDisplayManagerCallback = new MyDisplayManagerCallback(this);

            startMyIsolatedService(MainApplication.this, false );
        }

 */
    }

    private boolean isMainProcess( Context context ) {
        try {
            int pid = android.os.Process.myPid();
            ActivityManager manager = ( ActivityManager )this.getSystemService( Context.ACTIVITY_SERVICE );
            String packageName = context.getPackageName();
            String currentProcessName = null;

            Log.d( TAG, String.format( "PID:%d, PackageName:%s", pid, packageName ));
            for( ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses() ) {
                if ( processInfo.pid != pid ) continue;

                currentProcessName = processInfo.processName;
                Log.d( TAG, String.format( "currentProcessName:%s", currentProcessName ));

                if( packageName.compareTo( currentProcessName ) == 0 ) return true;
            }

            if( currentProcessName != null ) {
                PackageInfo pinfo = context.getPackageManager().getPackageInfo(packageName, 0);
                return currentProcessName.compareTo( pinfo.applicationInfo.processName ) == 0;
            }

        } catch( SecurityException e ) {
        } catch( Exception e ) {
        }

        return false;
    }

    private Messenger messenger = new Messenger( new MyHandler() );
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            Bundle data = msg.getData();
            String resultString = data.getString( "result" );

            Log.d( TAG, String.format( "Handler::result => what:%d, msg:%s", msg.what, resultString ));
        }
    }


    private void startMyIsolatedService( Context context, boolean useBind ) {
        Intent intent = new Intent( context.getApplicationContext(), MyIsolatedService.class);
        intent.putExtra( "messenger",  messenger );
        if( useBind ) {
            context.getApplicationContext().bindService(intent, myServiceConnection, BIND_AUTO_CREATE);
        } else {
            context.startService(intent);
        }
    }

    private ServiceConnection myServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "ServiceConnection::onServiceConnected");

            IIsolatedService binder = IIsolatedService.Stub.asInterface(iBinder);
            try {
                binder.isMagiskPresent();
            } catch (RemoteException e) {
                e.printStackTrace();

            } catch( SecurityException e ) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "ServiceConnection::onServiceDisconnected");
        }
    };
}
