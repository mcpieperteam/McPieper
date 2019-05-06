package com.mcpieperteam.mcpieper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import static com.mcpieperteam.mcpieper.NotificationProvider.CHANNEL_ID_dienst;

public class autostart extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            SharedPreferences preferences = context.getSharedPreferences("refresh", 0);
            boolean brdserviece = preferences.getBoolean("bgservice", false);
            if (brdserviece) {
                try {
                    Intent intent_start = new Intent(context, NotificationMgr.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(intent_start);
                    } else {
                        context.startService(intent_start);
                    }

                } catch (Exception e) {
                    showNotification("Error", "Bitte starte die McPieper App!!!", context);
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotification(String title, String message, Context context) {


        Intent intent = new Intent(context, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_dienst)
                .setSmallIcon(R.drawable.mcpieper_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        notificationManager.notify(2, builder.build());
    }

}



