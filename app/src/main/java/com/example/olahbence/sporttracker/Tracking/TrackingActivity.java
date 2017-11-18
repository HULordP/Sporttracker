package com.example.olahbence.sporttracker.Tracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.olahbence.sporttracker.R;
import com.example.olahbence.sporttracker.Tracking.Database.Track;
import com.example.olahbence.sporttracker.Tracking.Services.ServiceLocation;
import com.example.olahbence.sporttracker.Tracking.Services.ServiceTime;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static java.lang.Double.parseDouble;

public class TrackingActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String BR_NEW_RESTART = "BR_NEW_RESTART";
    public static final String KEY_RESTART = "KEY_RESTART";
    public static final String BR_STOP = "BR_STOP";
    private TextView mTime;
    private TextView mDistance;
    private TextView mAveragePace;
    private TextView mCurrentPace;
    private Location mLastKnownLocation;
    private Location mBeforeLastLocation;
    private TextView mPositionTime;
    private long mTimeTime;
    private GoogleMap mMap;
    private int DEFAULT_ZOOM = 16;
    private boolean map = false;
    private SupportMapFragment main;
    private float f1;
    private boolean focus = true;
    private File mLocations;
    private PolylineOptions mPolylineOptions;
    private Polyline mPolyline;
    private boolean polyline_added = false;
    private LatLng prev;
    private long averagePace;
    private FirebaseDatabase mDatabase;
    private BroadcastReceiver mLocationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mLastKnownLocation != null) {
                mBeforeLastLocation = mLastKnownLocation;
            }

            mLastKnownLocation = intent.getParcelableExtra(ServiceLocation.KEY_LOCATION);

            if (mBeforeLastLocation != null) {
                if (map) {
                    LatLng currentLocation = new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude());
                    if (focus) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                currentLocation, DEFAULT_ZOOM));
                    }
                }
            }

            double d1;
            int i2;
            String s1;
            String s2;
            String s3;
            String s4;
            String toDisplay;


            d1 = mLastKnownLocation.getSpeed();
            if (d1 != 0) {
                double d2 = 16.0 + (2.0 / 3);
                s1 = getString(R.string.current_pace);
                s2 = Double.toString(d2 / d1);
                i2 = s2.indexOf(".");
                s3 = s2.substring(0, i2);
                s4 = "0." + s2.substring(i2 + 1);
                d2 = parseDouble(s4);
                d2 = 60 * d2;
                s4 = String.format(Locale.ENGLISH, "%.0f", d2);
                toDisplay = s1 + "\n" + s3 + " : " + s4 + " min/km";
                mCurrentPace.setText(toDisplay);
            }
            if (d1 == 0) {
                s1 = getString(R.string.current_pace);
                toDisplay = s1 + "\n" + " -- ";
                mCurrentPace.setText(toDisplay);
            }


            s1 = getString(R.string.position_time);
            s2 = new Date(mLastKnownLocation.getTime()).toString();
            toDisplay = s1 + "\n" + s2;
            mPositionTime.setText(toDisplay);
        }
    };
    private BroadcastReceiver mLatLongReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (map) {
                LatLng current = intent.getParcelableExtra(ServiceLocation.KEY_LAT_LONG);
                if (!polyline_added) {
                    prev = current;
                    polyline_added = true;
                }
                mPolylineOptions.add(prev, current);
                mPolylineOptions.visible(true);
                mPolyline = mMap.addPolyline(mPolylineOptions);
                prev = current;
            }
        }
    };
    private BroadcastReceiver mDistanceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String s1;
            String toDisplay;

            if (mBeforeLastLocation != null) {
                f1 = intent.getFloatExtra(ServiceLocation.KEY_DISTANCE, 0);
                s1 = getString(R.string.distance);
                if (f1 / 1000 > 0.1) {
                    toDisplay = s1 + "\n" + f1 / 1000 + " km";
                    mDistance.setText(toDisplay);
                } else {
                    toDisplay = s1 + "\n" + "0 km";
                    mDistance.setText(toDisplay);
                }

            }
        }
    };
    private BroadcastReceiver mAveragePaceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String toDisplay;
            String aux2;
            long l1 = intent.getLongExtra(ServiceLocation.KEY_AVERAGE_PACE, 0);
            aux2 = getString(R.string.avarage_pace);
            if (l1 != 0) {
                toDisplay = aux2 + "\n" + TimeUnit.SECONDS.toMinutes(l1)
                        + " : " + l1 % 60 + " min/km";
                mAveragePace.setText(toDisplay);
                averagePace = l1;
            } else {
                toDisplay = aux2 + "\n" + TimeUnit.SECONDS.toMinutes(averagePace)
                        + " : " + averagePace % 60 + " min/km";
                mAveragePace.setText(toDisplay);
            }
        }
    };
    private BroadcastReceiver mTimeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mTimeTime = intent.getLongExtra(ServiceTime.KEY_TIME, 0);

            String aux2;
            String toDisplay;

            long timeSecond = TimeUnit.MILLISECONDS.toSeconds(mTimeTime);
            aux2 = getString(R.string.time);
            toDisplay = aux2 + "\n" + TimeUnit.SECONDS.toMinutes(timeSecond) + " : "
                    + timeSecond % 60;
            mTime.setText(toDisplay);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        main = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapTracking);
        main.getMapAsync(this);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Tracking");
            ActionBar ab = getSupportActionBar();
            ab.setDisplayHomeAsUpEnabled(true);
        }

        mTime = findViewById(R.id.time);
        mDistance = findViewById(R.id.distance);
        mAveragePace = findViewById(R.id.avarage_pace);
        mCurrentPace = findViewById(R.id.current_pace);
        mPositionTime = findViewById(R.id.positionTimeTest);

        String toDisplay;
        String aux2;
        aux2 = getString(R.string.avarage_pace);
        toDisplay = aux2 + "\n"
                + "--";
        mAveragePace.setText(toDisplay);

        String s1 = getString(R.string.distance);
        toDisplay = s1 + "\n" + 0 + " km";
        mDistance.setText(toDisplay);


        mPolylineOptions = new PolylineOptions().color(Color.BLUE)
                .width(5);

        Intent i = new Intent(getApplicationContext(), ServiceLocation.class);
        startService(i);
        Intent ii = new Intent(getApplicationContext(), ServiceTime.class);
        startService(ii);

        mDatabase = FirebaseDatabase.getInstance();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Button save = findViewById(R.id.save);
                if (save.getVisibility() != View.GONE) {
                    Intent ii = new Intent(this, DeleteTrack.class);
                    startActivityForResult(ii, 0);
                    return (true);
                }
        }

        return (super.onOptionsItemSelected(item));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            Intent i = new Intent(getApplicationContext(), ServiceLocation.class);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(
                    mLocationReceiver);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(
                    mDistanceReceiver);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(
                    mAveragePaceReceiver);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(
                    mTimeReceiver);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(
                    mLatLongReceiver);
            stopService(i);
            File averagePaces;
            String filename = "Average_Paces";
            String filePath = getApplicationContext().getFilesDir().getPath()
                    + File.separator + filename + ".txt";
            averagePaces = new File(filePath);
            boolean delete = averagePaces.delete();
            filename = "Locations_LatLong";
            filePath = getApplicationContext().getFilesDir().getPath()
                    + File.separator + filename + ".txt";
            mLocations = new File(filePath);

            delete = mLocations.delete();
            finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mLocationReceiver,
                new IntentFilter(ServiceLocation.BR_NEW_LOCATION));
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mAveragePaceReceiver,
                new IntentFilter(ServiceLocation.BR_NEW_AVERAGE_PACE));
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mDistanceReceiver,
                new IntentFilter(ServiceLocation.BR_NEW_DISTANCE));
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mTimeReceiver,
                new IntentFilter(ServiceTime.BR_NEW_TIME));
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mLatLongReceiver,
                new IntentFilter(ServiceLocation.BR_NEW_LAT_LONG));
        Intent intent = new Intent(BR_NEW_RESTART);
        intent.putExtra(KEY_RESTART, true);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    @Override
    public void onRestart() {
        super.onRestart();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mLocationReceiver,
                new IntentFilter(ServiceLocation.BR_NEW_LOCATION));
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mAveragePaceReceiver,
                new IntentFilter(ServiceLocation.BR_NEW_AVERAGE_PACE));
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mDistanceReceiver,
                new IntentFilter(ServiceLocation.BR_NEW_DISTANCE));
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mTimeReceiver,
                new IntentFilter(ServiceTime.BR_NEW_TIME));
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mLatLongReceiver,
                new IntentFilter(ServiceLocation.BR_NEW_LAT_LONG));
        RelativeLayout rl = findViewById(R.id.relative_layout);
        rl.setVisibility(View.VISIBLE);
        main.getMapAsync(this);
        Intent intent = new Intent(BR_NEW_RESTART);
        intent.putExtra(KEY_RESTART, true);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        String filename = "Locations_LatLong";
        String filepath = getApplicationContext().getFilesDir().getPath() + File.separator + filename + ".txt";
        new loadLocations().execute(filepath);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent i = new Intent(getApplicationContext(), ServiceLocation.class);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mLocationReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mDistanceReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mAveragePaceReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mTimeReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mLatLongReceiver);
        stopService(i);
        File averagePaces;
        String filename = "Average_Paces";
        String filePath = getApplicationContext().getFilesDir().getPath()
                + File.separator + filename + ".txt";
        averagePaces = new File(filePath);
        boolean delete = averagePaces.delete();
        filename = "Locations_LatLong";
        filePath = getApplicationContext().getFilesDir().getPath()
                + File.separator + filename + ".txt";
        mLocations = new File(filePath);
        delete = mLocations.delete();
        finish();
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mLocationReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mDistanceReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mAveragePaceReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mTimeReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mLatLongReceiver);

        map = false;
        Intent intent = new Intent(BR_STOP);
        intent.putExtra(BR_STOP, true);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        map = true;

    }

    public void focus(View view) {
        focus = !focus;
        view.setActivated(!view.isActivated());
    }

    public void toSave(View view) {
        Intent i = new Intent(getApplicationContext(), ServiceLocation.class);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mLocationReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mDistanceReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mAveragePaceReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mTimeReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mLatLongReceiver);
        stopService(i);

        Button save = findViewById(R.id.save);
        save.setVisibility(View.GONE);
        AVLoadingIndicatorView avLoadingIndicatorView = findViewById(R.id.saving_avi);
        avLoadingIndicatorView.setVisibility(View.VISIBLE);

        String filename = "Locations_LatLong";
        String filePath = getApplicationContext().getFilesDir().getPath() + File.separator + filename + ".txt";
        mLocations = new File(filePath);


        File mTrackUpload;
        filename = "Track_Upload";
        filePath = getApplicationContext().getFilesDir().getPath() + File.separator + filename + ".txt";
        mTrackUpload = new File(filePath);

        boolean delete = mTrackUpload.delete();
        try {
            boolean created = mTrackUpload.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }


        File averagePaces;
        filename = "Average_Paces";
        filePath = getApplicationContext().getFilesDir().getPath() + File.separator + filename + ".txt";
        averagePaces = new File(filePath);

        try {
            FileWriter mFileWriter = new FileWriter(mTrackUpload, true);
            long timeSecond = TimeUnit.MILLISECONDS.toSeconds(mTimeTime);
            mFileWriter.write(TimeUnit.SECONDS.toMinutes(timeSecond) + ":" + timeSecond % 60 + "\n");
            mFileWriter.write(String.valueOf(f1 / 1000) + "\n");

            try {
                FileReader mFileReader_Paces = new FileReader(averagePaces);
                BufferedReader bufferedReader_Pace = new BufferedReader(mFileReader_Paces);
                String paces;
                try {
                    while ((paces = bufferedReader_Pace.readLine()) != null) {
                        mFileWriter.write(paces + ",");
                    }
                    bufferedReader_Pace.close();
                    mFileReader_Paces.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                delete = averagePaces.delete();
                mFileWriter.write("\n");

                FileReader mFileReader_LatLong = new FileReader(mLocations);
                BufferedReader bufferedReader_LatLong = new BufferedReader(mFileReader_LatLong);
                String LatLong_String;
                try {
                    while ((LatLong_String = bufferedReader_LatLong.readLine()) != null) {
                        mFileWriter.write(LatLong_String + "\n");
                    }
                    bufferedReader_LatLong.close();
                    mFileReader_LatLong.close();
                    mFileWriter.write("\n");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                delete = mLocations.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mFileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String ID = user.getUid();
            long date = Calendar.getInstance().getTimeInMillis();

            filename = ID + "_" + date;


            String tracks;
            tracks = filename;
            Track toUploadTrack = new Track(tracks);
            toUploadTrack.setDistance(String.valueOf(f1 / 1000));
            long timeSecond = TimeUnit.MILLISECONDS.toSeconds(mTimeTime);
            String timeToUpload = TimeUnit.SECONDS.toMinutes(timeSecond) + ":" + timeSecond % 60;
            toUploadTrack.setTime(timeToUpload);

            String key = mDatabase.getReference("Tracks").child(ID).push().getKey();
            DatabaseReference myRef = mDatabase.getReference("Tracks").child(ID).child(key).child("filename");
            myRef.setValue(toUploadTrack.getFilename());
            myRef = mDatabase.getReference("Tracks").child(ID).child(key).child("date");
            myRef.setValue(date);
            myRef = mDatabase.getReference("Tracks").child(ID).child(key).child("distance");
            myRef.setValue(toUploadTrack.getDistance());
            myRef = mDatabase.getReference("Tracks").child(ID).child(key).child("time");
            myRef.setValue(toUploadTrack.getTime());


            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference trackTxtRef = storageRef.child("Tracks" + File.separator + filename + ".txt");


            Uri file = Uri.fromFile(mTrackUpload);

            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("text/plain")
                    .build();

            UploadTask uploadTask = trackTxtRef.putFile(file, metadata);

            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    System.out.println("Upload is " + progress + "% done");
                }
            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                    System.out.println("Upload is paused");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    AVLoadingIndicatorView avLoadingIndicatorView = findViewById(R.id.saving_avi);
                    avLoadingIndicatorView.setVisibility(View.GONE);
                    LinearLayout linearLayout = findViewById(R.id.lin_lay);
                    Snackbar.make(linearLayout, "Your upload is finished!", Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }

    private class loadLocations extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String filePath = strings[0];
            File file = new File(filePath);
            String result = "1";
            try {
                FileReader filereader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(filereader);
                String line;
                try {
                    while ((line = bufferedReader.readLine()) != null) {
                        try {
                            String[] temp = line.split(",");
                            if (!temp[0].isEmpty()) {
                                LatLng temp2 = new LatLng(Double.parseDouble(temp[0])
                                        , Double.parseDouble(temp[1]));
                                result = line;
                                if (!polyline_added) {
                                    prev = temp2;
                                    polyline_added = true;
                                }
                                mPolylineOptions.add(prev, temp2);
                                prev = temp2;
                            }
                        } catch (Exception e) {
                            Log.e("Exception: %s", e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                bufferedReader.close();
                filereader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.length() > 2) {
                String[] temp = result.split(",");
                LatLng camera = new LatLng(Double.parseDouble(temp[0])
                        , Double.parseDouble(temp[1]));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        camera, DEFAULT_ZOOM));
            }
            mPolylineOptions.visible(true);
            mPolyline = mMap.addPolyline(mPolylineOptions);
            RelativeLayout rl = findViewById(R.id.relative_layout);
            rl.setVisibility(View.GONE);
        }
    }


}
