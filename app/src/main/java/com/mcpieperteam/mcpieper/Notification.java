package com.mcpieperteam.mcpieper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.mcpieperteam.mcpieper.R;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Random;

public class Notification extends AppCompatActivity implements View.OnClickListener {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification);

        Button cncl = findViewById(R.id.cancel_serv);
        Button keep = findViewById(R.id.keep_serv);
        cncl.setOnClickListener(this);
        keep.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.keep_serv:
                String[] answrs = getResources().getStringArray(R.array.accept);
                SharedPreferences preferences = getSharedPreferences("refresh", 0);
                SharedPreferences.Editor edit = preferences.edit();
                Date d = new Date();
                edit.putInt("last_h", d.getHours());
                edit.putInt("last_d", d.getDay());
                edit.putInt("last_year", d.getYear());
                edit.putInt("last_month", d.getMonth());
                edit.commit();
                Snackbar.make(v, answrs[new Random().nextInt(answrs.length)], Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        //Your task it will execute at 1 time only...
                        finish();
                    }
                }, 3500);

                break;
            case R.id.cancel_serv:
                final View view = v;
                final Context ctx = this;
                final SharedPreferences sharedPreferences = getSharedPreferences("login", 0);
                final String usr = sharedPreferences.getString("usr", "");
                final String pwd = sharedPreferences.getString("pwd", "");
                (new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            URL url = new URL("http://jusax.dnshome.de/s/" + "sani.php?d=&act=e&un=" + usr + "&key=" + pwd);
                            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                            try {
                                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                                StringBuilder total = new StringBuilder();
                                String line;
                                while ((line = r.readLine()) != null) {
                                    total.append(line);
                                }
                                // 972 s, 971 hgk,973 e,974 zuspaet
                                String result = total.toString();
                                if (result.contains("972")) {
                                    String[] answrs = getResources().getStringArray(R.array.deny);

                                    Snackbar.make(view, answrs[new Random().nextInt(answrs.length)], Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                    SharedPreferences preferences = getSharedPreferences("refresh", 0);
                                    SharedPreferences.Editor edit = preferences.edit();
                                    Date d = new Date();
                                    edit.putInt("last_h", d.getHours());
                                    edit.putInt("last_d", d.getDay());
                                    edit.putInt("last_year", d.getYear());
                                    edit.putInt("last_month", d.getMonth());
                                    edit.commit();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {

                                                @Override
                                                public void run() {
                                                    //Your task it will execute at 1 time only...
                                                    finish();
                                                }
                                            }, 2000);
                                        }
                                    });
                                } else if (result.contains("971")) {
                                    String[] answrs = getResources().getStringArray(R.array.liar);

                                    Snackbar.make(view, answrs[new Random().nextInt(answrs.length)], Snackbar.LENGTH_LONG)
                                            .setAction("Click me", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    String[] answrs = getResources().getStringArray(R.array.snackbar_click_joke);
                                                    Snackbar.make(view, answrs[new Random().nextInt(answrs.length)], Snackbar.LENGTH_LONG);

                                                }
                                            }).show();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {

                                                @Override
                                                public void run() {
                                                    //Your task it will execute at 1 time only...
                                                    finish();
                                                }
                                            }, 2000);
                                        }
                                    });
                                } else if (result.contains("973")) {
                                    String[] answrs = getResources().getStringArray(R.array.error_while_canceling);

                                    Snackbar.make(view, answrs[new Random().nextInt(answrs.length)], Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {

                                                @Override
                                                public void run() {
                                                    //Your task it will execute at 1 time only...
                                                    finish();
                                                }
                                            }, 2000);
                                        }
                                    });
                                } else if (result.contains("974")) {
                                    String[] answrs = {"Du hast immernoch Dienst",
                                            "Du hast zu spät abgesagt", "Zu spät... Du hast immernoch Dienst",
                                            "Du musst zwischen 16 Uhr und 7 Uhr absagen!!!"};

                                    Snackbar.make(view, answrs[new Random().nextInt(answrs.length)], Snackbar.LENGTH_LONG)
                                            .setAction("Action", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    String[] answrs = getResources().getStringArray(R.array.snackbar_click_joke);
                                                    Snackbar.make(view, answrs[new Random().nextInt(answrs.length)], Snackbar.LENGTH_LONG);

                                                }
                                            }).show();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {

                                                @Override
                                                public void run() {
                                                    //Your task it will execute at 1 time only...
                                                    finish();
                                                }
                                            }, 2000);
                                        }
                                    });
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

                break;
        }
    }
}
