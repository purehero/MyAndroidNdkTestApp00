package com.purehero.myndkapp00;

public class Utils {
    public static void killMyProcess( int killTimeSec ) {
        if( killTimeSec < 1 ) {
            android.os.Process.killProcess(android.os.Process.myPid());
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep( killTimeSec * 1000 );
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }).start();
        }
    }
}
