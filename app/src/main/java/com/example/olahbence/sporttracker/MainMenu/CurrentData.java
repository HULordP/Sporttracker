package com.example.olahbence.sporttracker.MainMenu;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.example.olahbence.sporttracker.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Date;

public class CurrentData extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private TextView mLongitude;
    private TextView mLatitude;
    private TextView mSpeed;
    private TextView mHeight;
    private TextView mPositionTime;
    private TextView mTechnology;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_data);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Current data");
        mLongitude = findViewById(R.id.longitude);
        mLatitude = findViewById(R.id.latitude);
        mSpeed = findViewById(R.id.speed);
        mHeight = findViewById(R.id.height);
        mTechnology = findViewById(R.id.technology);
        mPositionTime = findViewById(R.id.positionTime);
        getLocationPermission();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    mLastKnownLocation = location;
                    editTextViews();
                }
            }
        };
        getDeviceLocation();
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        getDeviceLocation();
    }

    private void getLocationPermission() {

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),

                android.Manifest.permission.ACCESS_FINE_LOCATION)

                == PackageManager.PERMISSION_GRANTED) {

            mLocationPermissionGranted = true;

        } else {

            ActivityCompat.requestPermissions(this,

                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},

                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        }
    }

    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                            editTextViews();
                            createLocationRequest();
                            startLocationUpdates();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void editTextViews() {
        double aux;
        String aux2;
        String aux3;
        String aux4;
        int index;
        String toDisplay;

        aux = mLastKnownLocation.getLatitude();
        aux4 = Double.toString(aux);
        if (aux4.length() > 6) {
            index = aux4.indexOf(".");
            aux4 = aux4.substring(0, index + 5);
        }
        aux2 = getString(R.string.latitude);
        toDisplay = aux2 + "\n" + aux4;
        mLatitude.setText(toDisplay);

        aux = mLastKnownLocation.getLongitude();
        aux4 = Double.toString(aux);
        if (aux4.length() > 6) {
            index = aux4.indexOf(".");
            aux4 = aux4.substring(0, index + 5);
        }
        aux2 = getString(R.string.longitude);
        toDisplay = aux2 + "\n" + aux4;
        mLongitude.setText(toDisplay);

        aux = mLastKnownLocation.getSpeed();
        aux4 = Double.toString(aux);
        if (aux4.length() > 6) {
            index = aux4.indexOf(".");
            aux4 = aux4.substring(0, index + 5);
        }
        aux2 = getString(R.string.speed);
        toDisplay = aux2 + "\n" + aux4;
        mSpeed.setText(toDisplay);

        aux = mLastKnownLocation.getAltitude();
        aux4 = Double.toString(aux);
        if (aux4.length() > 6) {
            index = aux4.indexOf(".");
            aux4 = aux4.substring(0, index + 5);
        }
        aux2 = getString(R.string.height);
        toDisplay = aux2 + "\n" + aux4;
        mHeight.setText(toDisplay);

        aux2 = getString(R.string.position_time);
        aux3 = new Date(mLastKnownLocation.getTime()).toString();
        toDisplay = aux2 + "\n" + aux3;
        mPositionTime.setText(toDisplay);

        aux3 = mLastKnownLocation.getProvider();
        aux2 = getString(R.string.technology);
        toDisplay = aux2 + "\n" + aux3;
        mTechnology.setText(toDisplay);

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(2500);
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
}

