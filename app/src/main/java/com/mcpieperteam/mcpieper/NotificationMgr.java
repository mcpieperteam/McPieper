package com.cloudway.mcpieper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.cloudway.mcpieper.R;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

public class NotificationMgr extends Service {
    //hi

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onDestroy() {

        Toast.makeText(this, R.string.restart_app, Toast.LENGTH_LONG).show();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("crash","new start");
                startService(new Intent(getApplicationContext(), keeper.class));

            }
        });
        thread.start();

    }

    @Override
    public void onStart(Intent intent, int startid) {
        final Context ctx = this;
        SharedPreferences sharedPreferences = getSharedPreferences("login", 0);
        final String usr = sharedPreferences.getString("usr", "");
        final String pwd = sharedPreferences.getString("pwd", "");
        if (sharedPreferences.getBoolean("authed", false)) {
            final Handler timer = new Handler();
            timer.postDelayed(new Runnable() {
                @Override
                public void run() {
                    final SharedPreferences preferences = getSharedPreferences("refresh", 0);
                    Date d = new Date();
                    int c_hour = d.getHours();
                    int c_day = d.getDay();
                    int c_year = d.getYear();
                    int c_month = d.getMonth();
                    int last_hour = preferences.getInt("last_h", 0);
                    int last_day = preferences.getInt("last_d", 0);
                    int last_month = preferences.getInt("last_month", 0);
                    int last_year = preferences.getInt("last_year", 0);


                    //if ((c_day == last_day && last_hour < 7 && c_hour >= 16 && last_hour != c_hour) || /*(c_day == last_day && last_hour >= 16 && c_hour < 7 && last_hour <= c_hour) ||*/ (c_day != last_day && (last_hour < 7 && c_hour >= 16 && last_hour != c_hour)) || (c_day != last_day && (last_hour >= 16 && c_hour <= 7 && last_hour != c_hour))) {
                    if((c_year > last_year) || (c_year == last_year && c_month > last_month) || (c_year == last_year && c_month == last_month && c_day > last_day+1) || (c_year == last_year && c_month == last_month && c_day == last_day+1 && (last_hour<16 || c_hour>15)) || (c_year == last_year && c_month == last_month && c_day == last_day && last_hour<16 && c_hour>15)){
                        (new Thread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void run() {

                                try {
                                    URL url = new URL("http://jusax.dnshome.de/s/" + "sani.php?d=&act=m&un=" + usr + "&key=" + pwd);
                                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                                    try {
                                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                                        BufferedReader r = new BufferedReader(new InputStreamReader(in));
                                        StringBuilder total = new StringBuilder();
                                        String line;
                                        while ((line = r.readLine()) != null) {
                                            total.append(line);
                                        }

                                        String result = total.toString();
                                        if (result.contains("951")) {
                                            Intent intents = new Intent(getBaseContext(), Notification.class);
                                            intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intents);
                                            showNotification("Du hast Dienst!", "Klick hier um zu aktualisieren");
                                        } else if (result.contains("952")) {

                                        } else {

                                        }

                                    } finally {
                                        urlConnection.disconnect();

                                    }
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }


                        })).start();
                    }
                    timer.postDelayed(this, 5 * 60000);
                }
            }, 0);
        }


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotification(String title, String message) {
        final Context context = this;

        Intent intent = new Intent(context, NotificationMgr.class);

        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default")
                .setSmallIcon(R.drawable.mcpieper_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("com.myApp");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "com.myApp",
                    "My App",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
        notificationManager.notify(2, builder.build());
    }

    @Override
    public boolean onUnbind(Intent intent) {

        return super.onUnbind(intent);
    }
}
