package com.mcpieperteam.mcpieper;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mcpieperteam.mcpieper.R;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

public class LoginActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        final Button login_btn = findViewById(R.id.login);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context ctx = LoginActivity.this;
                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        EditText usr_field = findViewById(R.id.usr_input);
                        EditText pwd_field = findViewById(R.id.pw_input);
                        try {
                            URL url = new URL("http://jusax.dnshome.de/s/sani.php?d=&act=nt&un="+usr_field.getText()+"&key="+pwd_field.getText());
                            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                            try {
                                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                                StringBuilder total = new StringBuilder();
                                String line;
                                while ((line = r.readLine()) != null) {
                                    total.append(line);
                                }
                                if (total.toString().contains("901")) {
                                    String[] answrs = {"Login erfolgreich",
                                            "Anmeldung erfolgreich"};

                                    Snackbar.make(login_btn, answrs[new Random().nextInt(answrs.length)], Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                    SharedPreferences sp = getSharedPreferences("login",0);
                                    SharedPreferences.Editor edit = sp.edit();
                                    edit.putString("usr",usr_field.getText().toString());
                                    edit.putString("pwd",pwd_field.getText().toString());
                                    edit.putBoolean("authed",true);
                                    edit.commit();
                                    startActivity(new Intent(ctx, MainActivity.class));

                                    Intent intent_w_one = new Intent(getApplicationContext(), Widget_one_Provider.class);
                                    intent_w_one.setAction("android.appwidget.action.APPWIDGET_UPDATE");
                                    int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), Widget_one_Provider.class));
                                    intent_w_one.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
                                    sendBroadcast(intent_w_one);
                                    finish();
                                } else {
                                    String[] answrs = {"Es traten Probleme bei der Anmeldung auf",
                                            "Nutzername/Passwort falsch","Login nicht erfolgreich"," Ein Fehler ist aufgetreten ist"};

                                    Snackbar.make(login_btn, answrs[new Random().nextInt(answrs.length)], Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
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
        });

    }
}
