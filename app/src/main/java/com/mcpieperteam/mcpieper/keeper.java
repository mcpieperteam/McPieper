package com.mcpieperteam.mcpieper;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

public class keeper extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public void onDestroy() {


    }

    @Override
    public void onStart(Intent intent, int startid) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent_start = new Intent(getApplicationContext(), NotificationMgr.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    getApplicationContext().startForegroundService(intent_start);
                } else {
                    getApplicationContext().startService(intent_start);
                }
            }
        });
    }
}
