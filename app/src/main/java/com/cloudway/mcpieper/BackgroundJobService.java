package com.cloudway.mcpieper;

import android.app.ActivityManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class BackgroundJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("Job", "run");
        try {
            Context context = getApplicationContext();
            if (!isServiceRunning(NotificationMgr.class, context)) {

                Intent intent_start = new Intent(context, NotificationMgr.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent_start);
                } else {
                    context.startService(intent_start);
                }
                Log.d("Job", "start serviece");
            }
        } catch (Exception ignored) {

        }

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    private boolean isServiceRunning(Class serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
