package com.example.olahbence.sporttracker.Tracking.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.olahbence.sporttracker.R;
import com.example.olahbence.sporttracker.Tracking.TrackingActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.TimeUnit;

import static android.app.Notification.VISIBILITY_PUBLIC;


public class ServiceLocation extends Service {
    public static final String BR_NEW_LOCATION = "BR_NEW_LOCATION";
    public static final String KEY_LOCATION = "KEY_LOCATION";
    public static final String BR_NEW_DISTANCE = "BR_NEW_DISTANCE";
    public static final String KEY_DISTANCE = "KEY_DISTANCE";
    public static final String BR_NEW_AVERAGE_PACE = "BR_NEW_AVERAGE_PACE";
    public static final String KEY_AVERAGE_PACE = "KEY_AVERAGE_PACE";
    public static final String BR_NEW_LAT_LONG = "BR_NEW_LAT_LONG";
    public static final String KEY_LAT_LONG = "KEY_LAT_LONG";
    private final int NOTIF_FOREGROUND_ID = 101;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback mLocationCallback;
    private boolean mLocationPermissionGranted;
    private LocationRequest mLocationRequest;
    private boolean stop = false;
    private Location mLastKnownLocation;
    private Location mBeforeLastLocation;
    private long mTimeTime;
    private float f1 = 0;
    private int i1 = 1;
    private long f2;
    private long averagePace;
    private BroadcastReceiver mTimeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mTimeTime = intent.getLongExtra(ServiceTime.KEY_TIME, 0);

            long timeSecond = TimeUnit.MILLISECONDS.toSeconds(mTimeTime);
            String toDisplay = TimeUnit.SECONDS.toMinutes(timeSecond) + " : " + timeSecond % 60;
            if (f1 / 1000 > 0.1) {
                updateNotification("Distance: " + f1 / 1000 + " km" + "   " +
                        "Time: " + toDisplay);
            } else {
                updateNotification("Distance: " + 0 + " km" + "   " +
                        "Time: " + toDisplay);
            }
        }
    };
    private BroadcastReceiver mRestartReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean restart = intent.getBooleanExtra(TrackingActivity.KEY_RESTART, false);
            if (restart) {
                Intent i = new Intent(BR_NEW_AVERAGE_PACE);
                i.putExtra(KEY_AVERAGE_PACE, averagePace);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
                Intent intent2 = new Intent(BR_NEW_DISTANCE);
                intent2.putExtra(KEY_DISTANCE, f1);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent2);
                stop = false;
            }
        }
    };
    private BroadcastReceiver mStopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stop = intent.getBooleanExtra(TrackingActivity.BR_STOP, false);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIF_FOREGROUND_ID, getMyNotification("starting..."));
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mTimeReceiver,
                new IntentFilter(ServiceTime.BR_NEW_TIME));
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mRestartReceiver,
                new IntentFilter(TrackingActivity.BR_NEW_RESTART));
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mStopReceiver,
                new IntentFilter(TrackingActivity.BR_STOP));
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLocationPermission();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (mLastKnownLocation != null) {
                        mBeforeLastLocation = mLastKnownLocation;
                    }
                    mLastKnownLocation = location;
                    Intent intent = new Intent(BR_NEW_LOCATION);
                    intent.putExtra(KEY_LOCATION, location);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                    WriterThread1 w1 = new WriterThread1("Locations_LatLong");
                    w1.start();


                    if (mBeforeLastLocation != null) {
                        f1 += mBeforeLastLocation.distanceTo(mLastKnownLocation);
                        if (!stop) {
                            Intent intent2 = new Intent(BR_NEW_DISTANCE);
                            intent2.putExtra(KEY_DISTANCE, f1);
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent2);
                        }
                    }


                    WriterThread2 w2 = new WriterThread2("Average_Paces");
                    w2.start();

                }
            }
        };

        createLocationRequest();

        startLocationUpdates();

        return START_STICKY;
    }

    private void getLocationPermission() {

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),

                android.Manifest.permission.ACCESS_FINE_LOCATION)

                == PackageManager.PERMISSION_GRANTED) {

            mLocationPermissionGranted = true;

        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
        try {
            if (mLocationPermissionGranted) {
                if (mFusedLocationProviderClient != null) {
                    mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
                            mLocationCallback,
                            null /* Looper */);
                }
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private Notification getMyNotification(String text) {
        Intent intent = new Intent(this, TrackingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(TrackingActivity.class);

        stackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        return new Notification.Builder(this)
                .setOngoing(true)
                .setContentTitle("Sport tracker")
                .setContentText(text)
                .setVisibility(VISIBILITY_PUBLIC)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent).build();
    }

    private void updateNotification(String text) {
        Notification notification = getMyNotification(text);
        NotificationManager notifMan = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notifMan != null) {
            notifMan.notify(NOTIF_FOREGROUND_ID, notification);
        }
    }

    private void cancelNotification() {
        NotificationManager notifMan = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notifMan != null) {
            notifMan.cancelAll();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFusedLocationProviderClient != null) {
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }
        Intent i = new Intent(getApplicationContext(), ServiceTime.class);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mTimeReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mStopReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mRestartReceiver);
        cancelNotification();
        stopService(i);
    }

    class WriterThread1 extends Thread {
        String filename;

        WriterThread1(String filename) {
            this.filename = filename;
        }

        public void run() {
            String LatLong_String = mLastKnownLocation.getLatitude()
                    + "," + mLastKnownLocation.getLongitude() + "\n";
            String filePath = getApplicationContext().getFilesDir().getPath()
                    + File.separator + filename + ".txt";
            File mLocations = new File(filePath);
            if (!mLocations.exists()) {
                try {
                    boolean created = mLocations.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                FileWriter mFileWriter = new FileWriter(mLocations, true);
                mFileWriter.write(LatLong_String);
                mFileWriter.close();
                if (!stop) {
                    Intent intent3 = new Intent(BR_NEW_LAT_LONG);
                    LatLng toSend = new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude());
                    intent3.putExtra(KEY_LAT_LONG, toSend);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent3);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class WriterThread2 extends Thread {
        String filename;

        WriterThread2(String filename) {
            this.filename = filename;
        }

        public void run() {
            String filePath = getApplicationContext().getFilesDir().getPath()
                    + File.separator + filename + ".txt";
            File mAveragePaces = new File(filePath);


            if (!mAveragePaces.exists()) {
                try {
                    boolean created = mAveragePaces.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (f1 >= i1 * 1000) {
                if (i1 == 1) {
                    long timeSecond = TimeUnit.MILLISECONDS.toSeconds(mTimeTime);
                    String average_pace = TimeUnit.SECONDS.toMinutes(timeSecond)
                            + ":" + timeSecond % 60 + "\n";
                    if (!stop) {
                        Intent intent4 = new Intent(BR_NEW_AVERAGE_PACE);
                        intent4.putExtra(KEY_AVERAGE_PACE, timeSecond);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent4);
                    }
                    averagePace = timeSecond;
                    try {
                        FileWriter mFileWriter = new FileWriter(mAveragePaces, true);
                        mFileWriter.write(average_pace);
                        mFileWriter.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    f2 = mTimeTime;
                    i1 += 1;
                } else {
                    long f3 = mTimeTime - f2;
                    long timeSecond = TimeUnit.MILLISECONDS.toSeconds(f3);
                    String average_pace = TimeUnit.SECONDS.toMinutes(timeSecond)
                            + ":" + timeSecond % 60 + "\n";
                    if (!stop) {
                        Intent intent4 = new Intent(BR_NEW_AVERAGE_PACE);
                        intent4.putExtra(KEY_AVERAGE_PACE, timeSecond);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent4);
                    }
                    averagePace = timeSecond;
                    try {
                        FileWriter mFileWriter = new FileWriter(mAveragePaces, true);
                        mFileWriter.write(average_pace);
                        mFileWriter.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    f2 = mTimeTime;
                    i1 += 1;
                }
            }
        }
    }


}
