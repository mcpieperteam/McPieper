package com.mcpieperteam.mcpieper;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Date;

import static com.mcpieperteam.mcpieper.NotificationProvider.CHANNEL_ID_dienst;

public class McFirebaseInstanceServiece extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("MyFirebase", "recieved");

       showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());

    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        SharedPreferences sharedPreferences = getSharedPreferences("login", 0);
        String old = sharedPreferences.getString("token","");
        sharedPreferences.edit().putString("token-old",old).commit();
        sharedPreferences.edit().putString("token",s).commit();
        Log.d("TOKEN", s);
        //sim: di-LqYFW-lI:APA91bETA95DABpWtKkMngcDj8MvidQoFfqt8ihUtAgREQI-bkyIJXEme1Rkw3DXiqbk48RE-BL9XlIijhCNibowNTFF6oe4r_T1g8OgY0aMh_Q90PX69q8J3yYeSwaqxMZvL4yB5izA
        //a7: d9yxWDLgOXY:APA91bG7lbWESDXtvgnOdILZM8yYLqgpIZ6NlyZ_hxprmWot_xisohDVCqlBLrqL_3jX02rKPjMgsAhBUQLH8ZJTM166Vt2UhrD75h9KIsr38qPenwPaRcKIQQvVBn42ZCByreYsNlA9
    }
    private void showNotification(String title, String message) {
        final Context context = this;

        Intent intent = new Intent(context, NotificationMgr.class);

        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        android.app.Notification builder = new NotificationCompat.Builder(this, CHANNEL_ID_dienst)
                .setSmallIcon(R.drawable.mcpieper_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(Color.rgb(205,100,100))
                .setContentIntent(pendingIntent)
                .build();


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify("2",2, builder);
    }

}
