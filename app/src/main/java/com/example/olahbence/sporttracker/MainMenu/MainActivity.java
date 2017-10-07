package com.example.olahbence.sporttracker.MainMenu;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.olahbence.sporttracker.R;
import com.example.olahbence.sporttracker.Result.ResultList.ResultsListActivity;
import com.example.olahbence.sporttracker.Tracking.TrackingActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private int DEFAULT_ZOOM = 16;
    private SupportMapFragment main;
    private boolean b1 = true;
    private Marker mMarker;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private GnssStatus.Callback mGnssStatusCallback;
    private int satelliteCount;
    private TextView gpsFix;
    private TextView satellite;
    private TextView satellite2;
    private int fixSatelliteCount;
    private String mSatellite;
    private String mSatelliteFix;
    private LocationManager locManager;
    private boolean b2 = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapMain);
        main.getMapAsync(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Main menu");
        gpsFix = (TextView) findViewById(R.id.gps_fixed);
        satellite = (TextView) findViewById(R.id.satellite_in_view);
        satellite2 = (TextView) findViewById(R.id.satellite_fixed);
        mSatellite = getString(R.string.number_of_satellites);
        mSatelliteFix = getString(R.string.number_of_fixed_satellites);
        String toDisplay;
        String aux = getString(R.string.gps_fixed);
        toDisplay = aux + " no";
        gpsFix.setText(toDisplay);
        toDisplay = mSatellite + "\n" + "0";
        satellite.setText(toDisplay);
        toDisplay = mSatelliteFix + "\n" + "0";
        satellite2.setText(toDisplay);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    mLastKnownLocation = location;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(mLastKnownLocation.getLatitude(),
                                    mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                    putMarker();
                    String aux = getString(R.string.gps_fixed);
                    if (mLastKnownLocation.getSpeed() != 0) {
                        String toDisplay = aux + " yes";
                        gpsFix.setText(toDisplay);
                    } else {
                        String toDisplay = aux + " no";
                        gpsFix.setText(toDisplay);
                    }
                }
            }
        };
        mGnssStatusCallback = new GnssStatus.Callback() {
            @Override
            public void onSatelliteStatusChanged(GnssStatus status) {
                super.onSatelliteStatusChanged(status);
                satelliteCount = status.getSatelliteCount();
                String toDisplay = mSatellite + "\n" + satelliteCount;
                satellite.setText(toDisplay);
                fixSatelliteCount = 0;
                int i = 0;
                while (i < satelliteCount) {
                    if (status.usedInFix(i)) {
                        fixSatelliteCount++;
                    }
                    i++;
                }

                toDisplay = mSatelliteFix + "\n" + fixSatelliteCount;
                satellite2.setText(toDisplay);
            }
        };
        locManager =
                (LocationManager) getSystemService(LOCATION_SERVICE);
        getLocationPermission();
        try {
            if (mLocationPermissionGranted) {
                locManager.registerGnssStatusCallback(mGnssStatusCallback);
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        if (b2) {
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        getDeviceLocation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onMapLongClick(LatLng point) {
        Intent i = new Intent(MainActivity.this, BigMapsActivity.class);
        startActivity(i);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(this);

        getLocationPermission();

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

    private void putMarker() {
        LatLng currentLocation = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
        MarkerOptions aux = new MarkerOptions().position(currentLocation);
        if (b1) {
            mMarker = mMap.addMarker(aux.title("Marker in current location"));
            b1 = false;
        }
        mMarker.setPosition(currentLocation);
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
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            putMarker();
                            createLocationRequest();
                            startLocationUpdates();
                        } else {
                            //TBD
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public void currentData(View view) {
        b2 = false;
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        Intent i = new Intent(MainActivity.this, CurrentData.class);
        startActivity(i);
    }

    public void startTrack(View view) {
        b2 = false;
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        Intent i = new Intent(MainActivity.this, TrackingActivity.class);
        startActivity(i);
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

    public void toResult(View view) {
        Intent i = new Intent(MainActivity.this, ResultsListActivity.class);
        startActivity(i);
    }
}
