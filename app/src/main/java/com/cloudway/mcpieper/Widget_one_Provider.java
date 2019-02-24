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

public class Widget_one_Provider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_one);
            /*views.setTextViewText(R.id.widget_one_content, "Kein Internet");
            views.setTextViewText(R.id.widget_one_titel, "Sanitäter(heute):");*/
            SharedPreferences sharedPreferences = context.getSharedPreferences("login", 0);

            if (sharedPreferences.getBoolean("authed", false)) {
                final String usr = sharedPreferences.getString("usr", "");
                final String pwd = sharedPreferences.getString("pwd", "");
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
                            views.setTextViewText(R.id.widget_one_titel, "Sanitäter(morgen):");
                        } else {
                            views.setTextViewText(R.id.widget_one_titel, "Sanitäter(heute):");
                        }
                        String group = "";
                        if (result.length != 3) {
                            views.setTextViewText(R.id.widget_one_content, "keiner");
                        } else {
                            views.setTextViewText(R.id.widget_one_content, group);
                        }
                    } finally {
                        urlConnection.disconnect();
                        views.setTextViewText(R.id.widget_one_content, "Kein Internet");
                        views.setTextViewText(R.id.widget_one_titel, "Sanitäter(?):");
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    views.setTextViewText(R.id.widget_one_content, "Kein Internet");
                    views.setTextViewText(R.id.widget_one_titel, "Sanitäter(?):");
                } catch (IOException e) {
                    e.printStackTrace();
                    views.setTextViewText(R.id.widget_one_content, "Kein Internet");
                    views.setTextViewText(R.id.widget_one_titel, "Sanitäter(?):");
                }
            }else{
                Intent intent = new Intent(context, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

                views.setTextViewText(R.id.widget_one_content, "Klicke einfach!!!");
                views.setTextViewText(R.id.widget_one_titel, "Bitte anmelden.");
                views.setOnClickPendingIntent(R.id.widget_one, pendingIntent);
            }
            Toast.makeText(context,"aktualiesiere", Toast.LENGTH_SHORT).show();
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
