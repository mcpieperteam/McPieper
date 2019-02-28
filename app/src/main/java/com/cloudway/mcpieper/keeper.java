package com.cloudway.mcpieper;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

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
                startService(new Intent(getApplicationContext(), NotificationMgr.class));
            }
        });
    }
}
