package com.rideshare.app.tracker;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.rideshare.app.pojo.Global;


public class TimerService extends Service {
    long seconds = 0L;
    public static Handler timerHandler = new Handler();
    public static Runnable timerRunnable;
    private final String CHANNEL_ID = "Channel_id";
    NotificationManager mNotificationManager;
    int prevSeconds;

    @SuppressLint({"InlinedApi", "ForegroundServiceType"})
    @Override
    public void onCreate() {
        try {
            super.onCreate();
            String TAG = "Timer Service";
            Log.d(TAG, "onCreate: started service");

            // This is used to update the notification
            startForeground(1, new NotificationCompat.Builder(TimerService.this, createChannel()).setContentTitle("Timer In Progress").setPriority(NotificationManager.IMPORTANCE_MAX).build());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            Global.setIsRunningInBackground(this, true);
            Global.setWasTimerRunning(this, true);

            prevSeconds = (int) Global.getTimerSecondsPassed(TimerService.this);

            seconds = prevSeconds;

            timerRunnable = new Runnable() {
                @Override
                public void run() {
                    // add a second to the counter
                    seconds++;
                    //Print the seconds
                    Log.d("timerCount", seconds + "");

                    //Save the seconds passed to shared preferences
                    Global.setTimerSecondsPassed(TimerService.this, seconds);

                    if (seconds < 300) {
                        timerHandler.postDelayed(this, 1000);
                    } else {
                        timerHandler.removeCallbacks(timerRunnable);
                        seconds = 0;

                        Global.setTimerSecondsPassed(TimerService.this, seconds);
                        Global.setIsRunningInBackground(TimerService.this, false);
                        Global.setWasTimerRunning(TimerService.this, false);
                        stopSelf();
                    }
                }
            };

            //Start the stop watch
            timerHandler.postDelayed(timerRunnable, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        try{
            timerHandler.removeCallbacks(timerRunnable);
            Global.setTimerSecondsPassed(this, seconds);
            Global.setIsRunningInBackground(this, false);
            super.onDestroy();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @NonNull
    @TargetApi(26)
    private synchronized String createChannel() {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        String name = "STOPWATCH";
        int importance = NotificationManager.IMPORTANCE_LOW;

        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

        mChannel.setName("Notifications");

        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(mChannel);
        } else {
            stopSelf();
        }

        return CHANNEL_ID;
    }

}
