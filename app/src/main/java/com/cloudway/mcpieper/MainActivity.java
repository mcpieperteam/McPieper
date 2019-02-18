package com.cloudway.mcpieper;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.cloudway.mcpieper.R;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //start AlarmManager in 10 sek.
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, Receiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+10000, pendingIntent);


        setContentView(R.layout.activity_main);
        final Context ctx = this;
        SharedPreferences sp = getSharedPreferences("login", 0);
        if (sp.getBoolean("authed", false) == false) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            startService(new Intent(this, NotificationMgr.class));
        }
        WallpaperManager wpm = WallpaperManager.getInstance(this);
        Drawable d = wpm.getDrawable();
        LinearLayout l = findViewById(R.id.bg);
        l.setBackground(d);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                //remove "Sanidienst"

                new AlertDialog.Builder(ctx)
                        .setIcon(R.drawable.mcpieper_icon)
                        .setTitle(R.string.alert_title)
                        .setMessage(R.string.alert_content)
                        .setPositiveButton(R.string.alert_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                SharedPreferences sharedPreferences = getSharedPreferences("login", 0);
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
                                                } else if (result.contains("971")) {
                                                    String[] answrs = getResources().getStringArray(R.array.liar);

                                                    Snackbar.make(view, answrs[new Random().nextInt(answrs.length)], Snackbar.LENGTH_LONG)
                                                            .setAction("Click me", new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    String[] answrs = getResources().getStringArray(R.array.snackbar_click_joke);
                                                                    Snackbar.make(v, answrs[new Random().nextInt(answrs.length)], Snackbar.LENGTH_LONG);

                                                                }
                                                            }).show();
                                                } else if (result.contains("973")) {
                                                    String[] answrs = getResources().getStringArray(R.array.error_while_canceling);

                                                    Snackbar.make(view, answrs[new Random().nextInt(answrs.length)], Snackbar.LENGTH_LONG)
                                                            .setAction("Action", null).show();
                                                } else if (result.contains("972")) {
                                                    String[] answrs = {"Opfer... Du hast immernoch Dienst",
                                                            "Unerfolgreich: Bist du ne Schildkröte oder weshalb sagst du zu spät ab?", "Zu spät... Du hast immernoch Dienst",
                                                            "Du weißt schon, dass du zwischen 16Uhr und 7Uhr absagen musst? Ist das zu viel verlangt?"};

                                                    Snackbar.make(view, answrs[new Random().nextInt(answrs.length)], Snackbar.LENGTH_LONG)
                                                            .setAction("Click me", new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    String[] answrs = getResources().getStringArray(R.array.snackbar_click_joke);
                                                                    Snackbar.make(view, answrs[new Random().nextInt(answrs.length)], Snackbar.LENGTH_LONG);

                                                                }
                                                            }).show();
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

                        })
                        .setNegativeButton(R.string.alert_no, null)
                        .show();

            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);

            }

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                TextView usr_name = findViewById(R.id.tv);
                SharedPreferences sp = ctx.getSharedPreferences("login", 0);
                String usr = sp.getString("usr", "");
                usr_name.setText(usr);
            }


        };

        drawer.addDrawerListener(toggle);

        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        ViewFlipper v = findViewById(R.id.disp);
        v.setDisplayedChild(0);


    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.change_pwd_btn) {
            final EditText old_pwd_in = (EditText) findViewById(R.id.old_pwd);
            final EditText new_pwd_in = (EditText) findViewById(R.id.new_pwd);
            final EditText new_pwd_conf_in = (EditText) findViewById(R.id.new_pwd_conf);
            final SharedPreferences sharedPreferences = getSharedPreferences("login", 0);
            final String new_pwd = new_pwd_in.getText().toString();
            String new_pwd_conf = new_pwd_conf_in.getText().toString();
            if (old_pwd_in.getText().toString().matches(sharedPreferences.getString("pwd", "")) && new_pwd.matches(new_pwd_conf_in.getText().toString())) {
                final String usr = sharedPreferences.getString("usr", "");
                final String pwd = sharedPreferences.getString("pwd", "");
                (new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            URL url = new URL("http://jusax.dnshome.de/s/" + "sani.php?d=" + new_pwd + "&act=cp&un=" + usr + "&key=" + pwd);
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
                                if (result.contains("941")) {
                                    String[] answrs = {"Passwort geändert",
                                            "Oh du bist intelligent genug um dein Kennwort zu ändern", "Dein Neues Kennwort ist ********"};

                                    Snackbar.make(v, answrs[new Random().nextInt(answrs.length)], Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("pwd", new_pwd);
                                    editor.commit();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            old_pwd_in.setText("");
                                            new_pwd_in.setText("");
                                            new_pwd_conf_in.setText("");
                                        }
                                    });
                                } else if (result.contains("942")) {
                                    String[] answrs = {"Bist du zu blöd dein Kennwort zu ändern?",
                                            "Haha es gab ein Problem beim ändern"};

                                    Snackbar.make(v, answrs[new Random().nextInt(answrs.length)], Snackbar.LENGTH_LONG)
                                            .setAction("Click me", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    String[] answrs = getResources().getStringArray(R.array.snackbar_click_joke);
                                                    Snackbar.make(v, answrs[new Random().nextInt(answrs.length)], Snackbar.LENGTH_LONG);

                                                }
                                            }).show();
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
            } else {
                Snackbar.make(v, "Ein Fehler ist aufgetreten", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("login", 0);
        final String usr = sharedPreferences.getString("usr", "");
        final String pwd = sharedPreferences.getString("pwd", "");
        (new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
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
                            result[1] = "Dienst haben morgen : \n";
                        } else {
                            result[1] = "Dienst haben heute : \n";
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String group = "";
                                if (result.length != 3) {
                                    group = "Wochenende";
                                } else {
                                    group = result[2];
                                }
                                TextView info_home = findViewById(R.id.info_home);
                                info_home.setText("Woche : " + result[0] + "\n" + result[1] + group);
                            }
                        });

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotification(String title, String message) {
        final Context context = this;

        Intent intent = new Intent(context, Notification.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


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
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        final Context ctx = this;
        ViewFlipper v = findViewById(R.id.disp);
        int id = item.getItemId();
        if (id == R.id.home) {


            v.setDisplayedChild(0);
        } else if (id == R.id.change) {
            SharedPreferences sharedPreferences = getSharedPreferences("login", 0);
            final String usr = sharedPreferences.getString("usr", "");
            final String pwd = sharedPreferences.getString("pwd", "");
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
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(ctx, Notification.class));
                                    }
                                });

                            } else if (result.contains("952")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        FloatingActionButton fab = findViewById(R.id.fab);
                                        Snackbar.make(fab, "Du hast keinen Dienst", Snackbar.LENGTH_LONG).show();
                                    }
                                });

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

        } else if (id == R.id.calender) {

        } else if (id == R.id.settings) {
            v.setDisplayedChild(1);
            WallpaperManager wpm = WallpaperManager.getInstance(this);
            Drawable d = wpm.getDrawable();
            LinearLayout l = findViewById(R.id.bg_settings);
            l.setBackground(d);
            ImageView imageButton = (ImageView) findViewById(R.id.change_pwd_btn);
            imageButton.setOnClickListener(this);
        } else if (id == R.id.playstore) {
            String url = R.string.main_playstore + "";

            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } else if (id == R.id.website) {
            String url = "http://jusax.dnshome.de/s/";

            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } else if (id == R.id.becomebeta) {
            String url = "https://play.google.com/apps/testing/com.cloudway.mcpieper";

            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
