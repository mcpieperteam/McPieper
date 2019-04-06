package com.mcpieperteam.mcpieper;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
        Log.d("MyFirebase", "reseived");

        Date d = new Date();
        String day = "morgen";
        if(d.getHours()<16){
            day = "heute";
        }
        if(!remoteMessage.getData().isEmpty()){
            showNotification("Du hast "+day+" Dienst", "... mit "+remoteMessage.getData().get("names")+"!!!");
        }else {
            showNotification("Du hast "+day+" Dienst", "Vergiss nicht den Pieper abzuholen!!!");
        }

    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
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
                .setContentIntent(pendingIntent)
                .build();


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(2, builder);
    }

}
