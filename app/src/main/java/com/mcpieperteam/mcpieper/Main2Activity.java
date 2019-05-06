package com.mcpieperteam.mcpieper;

import android.animation.LayoutTransition;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
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
import java.util.Date;
import java.util.Random;

import static com.mcpieperteam.mcpieper.MainActivity.JodScheduler_one;

public class Main2Activity extends AppCompatActivity {
    public ViewFlipper flipper;
    public BottomNavigationView navView;
    final public Context ctx = this;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    switch (flipper.getDisplayedChild()) {
                        case 1:
                            break;
                        case 0:
                            //notification (old tab)
                            flipper.setInAnimation(AnimationUtils.loadAnimation(ctx, R.anim.in_from_right));
                            flipper.setDisplayedChild(1);
                            break;
                        case 2:
                            //settings (old tab)
                            flipper.setInAnimation(AnimationUtils.loadAnimation(ctx, R.anim.in_from_left));
                            flipper.setDisplayedChild(1);
                            break;
                    }

                    return true;
                case R.id.navigation_settings:
                    flipper.setInAnimation(AnimationUtils.loadAnimation(ctx, R.anim.in_from_right));
                    flipper.setDisplayedChild(2);
                    final Button logout_btn = (Button) flipper.findViewById(R.id.request_logout);
                    final Dialog logout_confirm_dialog = new Dialog(ctx, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
                    logout_confirm_dialog.setContentView(R.layout.logout_confirm_popup);
                    //initialise popup buttons
                    Button cancel_logout = (Button) logout_confirm_dialog.findViewById(R.id.cancel_logout);
                    Button confirm_logout = (Button) logout_confirm_dialog.findViewById(R.id.confirm_logout);
                    //add click listener
                    cancel_logout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            logout_confirm_dialog.dismiss();
                        }
                    });
                    confirm_logout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Logout
                        }
                    });
                    //open confirmation on button press
                    logout_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            logout_confirm_dialog.show();
                        }
                    });
                    //RequestTechniqueButtons
                    Switch noFB = (Switch) flipper.findViewById(R.id.no_firebase_switch);
                    Switch power_saving_mode_swtch = (Switch) flipper.findViewById(R.id.save_energie_switch);
                    noFB.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Switch swit = flipper.findViewById(R.id.no_firebase_switch);
                            SharedPreferences preferences = getSharedPreferences("refresh", 0);
                            SharedPreferences.Editor edit = preferences.edit();
                            edit.putBoolean("bgservice", swit.isChecked());
                            Switch swit_o = findViewById(R.id.save_energie_switch);
                            if (swit.isChecked()) {

                                Snackbar.make(flipper, "Hintergrundservice aktiviert!", Snackbar.LENGTH_LONG).show();
                                swit_o.setVisibility(View.VISIBLE);
                                startService(new Intent(ctx, NotificationMgr.class));
                            } else {
                                Snackbar.make(flipper, "Hintergrundservice deaktiviert!", Snackbar.LENGTH_LONG).show();
                                swit_o.setVisibility(View.INVISIBLE);
                                stopService(new Intent(ctx, NotificationMgr.class));
                            }
                            edit.commit();
                        }
                    });
                    power_saving_mode_swtch.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Switch swit = findViewById(R.id.save_energie_switch);
                            SharedPreferences preferences = getSharedPreferences("refresh", 0);
                            SharedPreferences.Editor edit = preferences.edit();
                            edit.putBoolean("save_energie", swit.isChecked());
                            if (swit.isChecked()) {
                                Snackbar.make(flipper, "Energiesparmodus aktiviert!", Snackbar.LENGTH_LONG).show();
                                stopService(new Intent(ctx, NotificationMgr.class));
                            } else {
                                Snackbar.make(flipper, "Energiesparmodus deaktiviert!", Snackbar.LENGTH_LONG).show();
                                startService(new Intent(ctx, NotificationMgr.class));
                            }
                            edit.commit();
                        }
                    });
                    //apply switch states on load
                    SharedPreferences preferences = getSharedPreferences("refresh", 0);
                    noFB.setChecked(preferences.getBoolean("bgservice", false));
                    if (noFB.isChecked()) {
                        power_saving_mode_swtch.setVisibility(View.VISIBLE);
                    }
                    power_saving_mode_swtch.setChecked(preferences.getBoolean("save_energie", false));
                    //Change Password
                    Button request_pw_change = (Button) flipper.findViewById(R.id.changepw);
                    final Dialog pw_change_dialog = new Dialog(ctx, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
                    pw_change_dialog.setContentView(R.layout.popup_changepwd);
                    //initialise dialog interface
                    Button submit_pwd = (Button) pw_change_dialog.findViewById(R.id.submit_pw_change);
                    submit_pwd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            final EditText old_pwd_in = (EditText) pw_change_dialog.findViewById(R.id.inp_old_pwd);
                            final EditText new_pwd_in = (EditText) pw_change_dialog.findViewById(R.id.inp_new_pwd);
                            final EditText new_pwd_conf_in = (EditText) pw_change_dialog.findViewById(R.id.inp_new_pwd_rep);
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

                                                    Snackbar.make(flipper, answrs[new Random().nextInt(answrs.length)], Snackbar.LENGTH_LONG)
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
                                                            pw_change_dialog.dismiss();
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
                                                } else {
                                                    String[] answrs = {"Ein Problem trat auf beim ändern deines Kennworts",
                                                            "Es gab Probleme beim ändern"};

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
                    });
                    //show dialog on button press
                    request_pw_change.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pw_change_dialog.show();
                        }
                    });
                    //open website toggle
                    Button open_website = (Button) flipper.findViewById(R.id.openinbrowser);
                    open_website.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String url = "http://jusax.dnshome.de/s/";

                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                        }
                    });
                    //open in playstore
                    Button open_playstore = (Button) flipper.findViewById(R.id.openinplay);
                    open_playstore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("market://details?id=" + getPackageName())));
                            } catch (ActivityNotFoundException e) {
                                startActivity(new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                            }
                        }
                    });
                    //Theme Changer
                    Button change_theme_popup = (Button) flipper.findViewById(R.id.change_theme);
                    final Dialog theme_changer = new Dialog(ctx, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
                    theme_changer.setContentView(R.layout.popup_change_theme);
                    final SharedPreferences sharedPreferences = getSharedPreferences("style", 0);
                    final RadioGroup theme_selector = (RadioGroup) theme_changer.findViewById(R.id.theme_selctor);
                    change_theme_popup.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            theme_selector.check(sharedPreferences.getInt("theme", R.id.theme_default));
                            theme_changer.show();
                        }
                    });
                    //theme changer popup
                    Button submit_new_theme = (Button) theme_changer.findViewById(R.id.submit_theme_change);
                    submit_new_theme.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("theme", theme_selector.getCheckedRadioButtonId());
                            editor.commit();
                            startActivity(new Intent(ctx, Main2Activity.class));
                            finish();
                        }
                    });
                    return true;
                case R.id.navigation_notifications:
                    flipper.setInAnimation(AnimationUtils.loadAnimation(ctx, R.anim.in_from_left));
                    SharedPreferences usr_data = getSharedPreferences("login", 0);
                    final String usr = usr_data.getString("usr", "");
                    final String pwd = usr_data.getString("pwd", "");
                    //test if user is allowed here ;)
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
                                        //user can pass
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //open notfication section
                                                flipper.setDisplayedChild(0);

                                            }
                                        });
                                    } else if (result.contains("952")) {
                                        //stop user
                                        Snackbar.make(flipper, "Du hast keinen Dienst den du absagen kannst", Snackbar.LENGTH_LONG).show();
                                        navView.setSelectedItemId(R.id.navigation_home);

                                    } else {
                                        //stop user
                                        Snackbar.make(flipper, "Ein Fehler trat auf bitte Versuche es später erneut!", Snackbar.LENGTH_LONG).show();
                                        navView.setSelectedItemId(R.id.navigation_home);
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
                    //initialise layout
                    Button cncl = findViewById(R.id.cntnt_deny);
                    Button keep = findViewById(R.id.cntnt_accept);
                    keep.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
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
                        }
                    });
                    cncl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
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

                                                Snackbar.make(flipper, answrs[new Random().nextInt(answrs.length)], Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();
                                                SharedPreferences preferences = getSharedPreferences("refresh", 0);
                                                SharedPreferences.Editor edit = preferences.edit();
                                                Date d = new Date();
                                                edit.putInt("last_h", d.getHours());
                                                edit.putInt("last_d", d.getDay());
                                                edit.putInt("last_year", d.getYear());
                                                edit.putInt("last_month", d.getMonth());
                                                edit.commit();

                                            } else if (result.contains("971")) {
                                                String[] answrs = getResources().getStringArray(R.array.liar);

                                                Snackbar.make(flipper, answrs[new Random().nextInt(answrs.length)], Snackbar.LENGTH_LONG)
                                                        .setAction("Click me", new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                String[] answrs = getResources().getStringArray(R.array.snackbar_click_joke);
                                                                Snackbar.make(flipper, answrs[new Random().nextInt(answrs.length)], Snackbar.LENGTH_LONG);

                                                            }
                                                        }).show();



                                            } else if (result.contains("973")) {
                                                String[] answrs = getResources().getStringArray(R.array.error_while_canceling);

                                                Snackbar.make(flipper, answrs[new Random().nextInt(answrs.length)], Snackbar.LENGTH_LONG)
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

                                                Snackbar.make(flipper, answrs[new Random().nextInt(answrs.length)], Snackbar.LENGTH_LONG)
                                                        .setAction("Action", new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                String[] answrs = getResources().getStringArray(R.array.snackbar_click_joke);
                                                                Snackbar.make(flipper, answrs[new Random().nextInt(answrs.length)], Snackbar.LENGTH_LONG);

                                                            }
                                                        }).show();

                                            }
                                        } finally {
                                            urlConnection.disconnect();
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    navView.setSelectedItemId(R.id.navigation_home);
                                                }
                                            });

                                        }
                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            })).start();
                        }
                    });
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("style", 0);
        switch (sharedPreferences.getInt("theme", R.id.theme_default)) {
            case R.id.theme_dark:
                setTheme(R.style.AppTheme_Dark);
                break;
            case R.id.theme_transparent:
                setTheme(R.style.AppTheme_Transparent);
                break;
            case R.id.theme_default:
                setTheme(R.style.AppTheme);
        }
        ActionBar actionBar = getSupportActionBar();
        SharedPreferences sp = ctx.getSharedPreferences("login", 0);
        actionBar.setTitle("McPieper: " + sp.getString("usr", ""));
        setContentView(R.layout.activity_main2);


        flipper = (ViewFlipper) findViewById(R.id.view_flipper);
        flipper.setDisplayedChild(1);


        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navView.setSelectedItemId(R.id.navigation_home);
        flipper.setOnTouchListener(new OnSwipeTouchListener(Main2Activity.this) {
            @Override
            public void onSwipeRight() {
                flipper.setInAnimation(AnimationUtils.loadAnimation(ctx, R.anim.in_from_left));
                int p = navView.getSelectedItemId();
                switch (p) {
                    case R.id.navigation_home:
                        navView.setSelectedItemId(R.id.navigation_notifications);
                        break;
                    case R.id.navigation_notifications:
                        //Do nothing
                        break;
                    case R.id.navigation_settings:
                        navView.setSelectedItemId(R.id.navigation_home);
                        break;
                }
            }

            @Override
            public void onSwipeLeft() {
                flipper.setInAnimation(AnimationUtils.loadAnimation(ctx, R.anim.in_from_right));
                int p = navView.getSelectedItemId();
                switch (p) {
                    case R.id.navigation_home:
                        navView.setSelectedItemId(R.id.navigation_settings);
                        break;
                    case R.id.navigation_notifications:
                        navView.setSelectedItemId(R.id.navigation_home);
                        break;
                    case R.id.navigation_settings:
                        //do nothing
                        break;
                }
            }

            @Override
            public void onSwipeBottom() {
                onResume();
                Toast refresh = new Toast(ctx);
                refresh.setGravity(Gravity.TOP, 0, 15);
                refresh.setDuration(Toast.LENGTH_SHORT);
                View toasted_layout = getLayoutInflater().inflate(R.layout.toast_refresh, null);
                refresh.setView(toasted_layout);
                refresh.show();
            }
        });


        if (sp.getBoolean("authed", false) == false) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {

            SharedPreferences preferences = getSharedPreferences("refresh", 0);
            boolean brdserviece = preferences.getBoolean("bgrserviece", false);
            if (brdserviece) {
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
                        if (!total.toString().equals("903")) {
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
                                    TextView info_home = flipper.findViewById(R.id.info_home);
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


}

