package com.example.olahbence.sporttracker.Tracking.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;

public class ServiceTime extends Service {


    public static final String BR_NEW_TIME = "BR_NEW_TIME";
    public static final String KEY_TIME = "KEY_TIME";
    long startTime = SystemClock.elapsedRealtime();
    private Handler timerHandler;
    private Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            try {
                long millis = SystemClock.elapsedRealtime() - startTime;
                Intent intent = new Intent(BR_NEW_TIME);
                intent.putExtra(KEY_TIME, millis);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            } finally {
                timerHandler.postDelayed(this, 1000);
            }
        }
    };

    public ServiceTime() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        timerHandler = new Handler();

        timerRunnable.run();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(timerRunnable);
    }

}
