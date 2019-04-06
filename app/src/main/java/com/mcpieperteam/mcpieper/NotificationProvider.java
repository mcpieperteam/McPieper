package com.mcpieperteam.mcpieper;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class NotificationProvider extends Application {
    public static final String CHANNEL_ID = "checking";
    public static final String CHANNEL_ID_dienst = "dienst";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel_1 = new NotificationChannel(
                    CHANNEL_ID,
                    "Sync McPieper",
                    //NotificationManager.IMPORTANCE_MIN,
                    NotificationManager.IMPORTANCE_MIN
            );
            serviceChannel_1.setShowBadge(false);
            ////
            NotificationChannel serviceChannel_2 = new NotificationChannel(
                    CHANNEL_ID_dienst,
                    "McPieper notice",
                    NotificationManager.IMPORTANCE_HIGH

            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel_1);
            manager.createNotificationChannel(serviceChannel_2);
        }
    }


}
