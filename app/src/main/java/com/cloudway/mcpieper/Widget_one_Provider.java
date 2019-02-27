package com.cloudway.mcpieper;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
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

public class Widget_one_Provider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, final AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (final int appWidgetId : appWidgetIds) {

            final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_one_style);
            /*views.setTextViewText(R.id.widget_one_content, "Kein Internet");
            views.setTextViewText(R.id.widget_one_titel, "Sanitäter(heute):");*/
            SharedPreferences sharedPreferences = context.getSharedPreferences("login", 0);

            if (sharedPreferences.getBoolean("authed", false)) {
                final String usr = sharedPreferences.getString("usr", "");
                final String pwd = sharedPreferences.getString("pwd", "");
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL("http://jusax.dnshome.de/s/" + "sani.php?d=&act=a&un=" + usr + "&key=" + pwd);
                            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                            try {
                                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                                StringBuilder total = new StringBuilder();
                                String line;
                                while ((line = r.readLine()) != null) {
                                    total.append(line);
                                }
                                final String[] result = total.toString().split(";");
                                if (result[1].contains("m")) {
                                    views.setTextViewText(R.id.widget_one_titel, "Sanitäter\n(morgen):");
                                } else {
                                    views.setTextViewText(R.id.widget_one_titel, "Sanitäter\n(heute):");
                                }
                                String group = "";
                                if (result.length != 3) {
                                    views.setTextViewText(R.id.widget_one_content, "keiner");
                                } else {
                                    views.setTextViewText(R.id.widget_one_content, result[2]);
                                }
                                Date d = new Date();
                                views.setTextViewText(R.id.widget_one_offline, "Zuletzt aktualisiert: "+String.valueOf(d.getHours())+":"+String.valueOf(d.getMinutes()));
                            } finally {
                                appWidgetManager.updateAppWidget(appWidgetId, views);
                                urlConnection.disconnect();

                            }
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                            views.setTextViewText(R.id.widget_one_content, "Kein Internet");
                            views.setTextViewText(R.id.widget_one_titel, "Sanitäter\n(?):");
                            appWidgetManager.updateAppWidget(appWidgetId, views);
                        } catch (IOException e) {
                            e.printStackTrace();
                            views.setTextViewText(R.id.widget_one_content, "Kein Internet");
                            views.setTextViewText(R.id.widget_one_titel, "Sanitäter\n(?):");
                            Date d = new Date();
                            views.setTextViewText(R.id.widget_one_offline, "(Offline) "+String.valueOf(d.getHours())+String.valueOf(d.getMinutes()));
                            appWidgetManager.updateAppWidget(appWidgetId, views);
                        }

                    }
                });
                thread.start();

            }else{
                Intent intent = new Intent(context, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

                views.setTextViewText(R.id.widget_one_content, "Klicke einfach!!!");
                views.setTextViewText(R.id.widget_one_titel, "Bitte anmelden.");
                views.setOnClickPendingIntent(R.id.widget_one, pendingIntent);
            }
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
