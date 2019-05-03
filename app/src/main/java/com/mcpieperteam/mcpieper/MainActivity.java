package com.mcpieperteam.mcpieper;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.appwidget.AppWidgetManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

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

    public final static Integer JodScheduler_one = 1;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        final Context ctx = this;
        SharedPreferences sp = getSharedPreferences("login", 0);
        if (sp.getBoolean("authed", false) == false) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {

            SharedPreferences preferences = getSharedPreferences("refresh", 0);
            boolean brdserviece = preferences.getBoolean("bgrserviece", false);
            if(brdserviece){
                startService(new Intent(this, NotificationMgr.class));
            }

            Intent intent_w_one = new Intent(this, Widget_one_Provider.class);
            intent_w_one.setAction("android.appwidget.action.APPWIDGET_UPDATE");
            int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), Widget_one_Provider.class));
            intent_w_one.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
            sendBroadcast(intent_w_one);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ComponentName componentName = new ComponentName(this, BackgroundJobService.class);
                JobInfo info = new JobInfo.Builder(JodScheduler_one, componentName)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .setPersisted(true)
                        .setPeriodic(15 * 60 * 1000)
                        .build();
                JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
                if (info != null) {
                    scheduler.cancel(JodScheduler_one);
                    int resulteCode = scheduler.schedule(info);
                    if (resulteCode == JobScheduler.RESULT_SUCCESS) {
                        Log.d("Job", "started");
                    } else {
                        Log.e("Job", "failed");
                    }
                } else {
                    Log.e("Job", "failed info");
                }
            }

        }

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
        Switch switchh = findViewById(R.id.save_enerdie_switch);
        switchh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChange_switch(v);
            }
        });
        findViewById(R.id.no_firebase_switch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChange_switch(v);
            }
        });

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
                                            "Dein neues Passwort ist: ******", "Dein Neues Kennwort ist ********"};

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
                                } else if (result.contains("942") || result.contains("943")) {


                                    Snackbar.make(v, "Das Kennwort darf keine Sonderzeichen enthalten!!!", Snackbar.LENGTH_LONG)
                                            .setAction("Click me", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    String[] answrs = getResources().getStringArray(R.array.snackbar_click_joke);
                                                    Snackbar.make(v, answrs[new Random().nextInt(answrs.length)], Snackbar.LENGTH_LONG);
                                                }
                                            }).show();
                                }else {
                                    String[] answrs = {"Ein Problem trat auf beim ändern deines Kennworts",
                                            "Problem beim ändern"};

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
        if(v.getId() == R.id.abmelden){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    public void onChange_switch(View v) {
        if (v.getId() == R.id.save_enerdie_switch) {
            Switch swit = findViewById(R.id.save_enerdie_switch);
            SharedPreferences preferences = getSharedPreferences("refresh", 0);
            SharedPreferences.Editor edit = preferences.edit();
            edit.putBoolean("save_engergie", swit.isChecked());
            if (swit.isChecked()) {
                Toast.makeText(this, "Energiesparmodus aktiviert!!!", Toast.LENGTH_LONG).show();
                stopService(new Intent(this, NotificationMgr.class));
            } else {
                Toast.makeText(this, "Energiesparmodus deaktiviert!!!", Toast.LENGTH_LONG).show();
                startService(new Intent(this, NotificationMgr.class));
            }
            edit.commit();
        }
        if(v.getId()==R.id.no_firebase_switch){
            Switch swit = findViewById(R.id.no_firebase_switch);
            SharedPreferences preferences = getSharedPreferences("refresh", 0);
            SharedPreferences.Editor edit = preferences.edit();
            edit.putBoolean("bgrserviece", swit.isChecked());
            Switch swit_o = findViewById(R.id.save_enerdie_switch);
            if (swit.isChecked()) {
                Toast.makeText(this, "Hintergrundservice aktiviert!!!", Toast.LENGTH_LONG).show();
                swit_o.setVisibility(View.VISIBLE);
                startService(new Intent(this, NotificationMgr.class));
            } else {
                Toast.makeText(this, "Hintergrundservice deaktiviert!!!", Toast.LENGTH_LONG).show();
                swit_o.setVisibility(View.INVISIBLE);
                stopService(new Intent(this, NotificationMgr.class));
            }
            edit.commit();
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
                        if (total.toString() != "903") {
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
                        } else {
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
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

        final String token = sharedPreferences.getString("token", "");
        final String old = sharedPreferences.getString("token-old", "");
        if (!token.equals("")) {
            (new Thread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void run() {


                    try {
                        URL url = new URL("http://jusax.dnshome.de/s/" + "sani.php?d=" + token + ";" + old + "&act=token&un=" + usr + "&key=" + pwd);
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        try {
                            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                            BufferedReader r = new BufferedReader(new InputStreamReader(in));
                            StringBuilder total = new StringBuilder();
                            String line;
                            while ((line = r.readLine()) != null) {
                                total.append(line);
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
            new AlertDialog.Builder(this)
                    .setTitle("Firebase ist nicht verfügbar!!!")
                    .setMessage("Stelle sicher, dass du eine Internetverbindung hast und starte die App neu. Wenn du diese Meldung wieder siehst, dann installiere die App neu.")
                    .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
        }
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

            final SharedPreferences preferences = getSharedPreferences("refresh", 0);
            boolean save_energie = preferences.getBoolean("save_engergie", false);
            boolean nofire = preferences.getBoolean("bgrserviece", false);
            Switch swit = findViewById(R.id.save_enerdie_switch);
            swit.setChecked(save_energie);
            if(nofire){
                swit.setVisibility(View.VISIBLE);
            }
            swit = findViewById(R.id.no_firebase_switch);
            swit.setChecked(nofire);
            ImageView imageButton = (ImageView) findViewById(R.id.change_pwd_btn);
            imageButton.setOnClickListener(this);
            Button button = (Button) findViewById(R.id.abmelden);
            button.setOnClickListener(this);
        } else if (id == R.id.playstore) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + getPackageName())));
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
            }
        } else if (id == R.id.website) {
            String url = "http://jusax.dnshome.de/s/";

            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } else if (id == R.id.becomebeta) {
            String url = "https://play.google.com/apps/testing/com.mcpieperteam.mcpieper";

            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
