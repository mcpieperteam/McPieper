package com.cloudway.mcpieper;

import android.app.AlarmManager;
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
import android.widget.Toast;

import java.util.Date;

public class autostart extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            try {
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent intent_alarm = new Intent(context, Receiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent_alarm, 0);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+10, pendingIntent);
                try{
                    Intent i = new Intent(context, NotificationMgr.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startService(i);
                    Toast.makeText(context, "Autostart", Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Toast.makeText(context, "McPieper halb gestartet!!!\n"+e.toString(), Toast.LENGTH_LONG).show();
                }
            }catch (Exception e){
                Toast.makeText(context, "Bitte starte Die McPieper app!!!", Toast.LENGTH_LONG).show();
            }
        }
    }

}



