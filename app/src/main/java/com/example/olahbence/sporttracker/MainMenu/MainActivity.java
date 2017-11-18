package com.example.olahbence.sporttracker.MainMenu;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.olahbence.sporttracker.Friends.Activities.FriendsActivities;
import com.example.olahbence.sporttracker.Friends.MyFriends.MyFriends;
import com.example.olahbence.sporttracker.Login.LoginActivity;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GoogleMap mMap;
    private boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private boolean b1 = true;
    private Marker mMarker;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private int satelliteCount;
    private TextView gpsFix;
    private TextView satellite;
    private TextView satellite2;
    private int fixSatelliteCount;
    private String mSatellite;
    private String mSatelliteFix;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private boolean isGpsFixed = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment main = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapMain);
        main.getMapAsync(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Main menu");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        mDrawerLayout = findViewById(R.id.drawer_layout);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            boolean showText = extras.getBoolean("Login", false);
            if (showText) {
                if (user != null) {
                    String userName = user.getDisplayName();
                    String aux = "Welcome " + userName;
                    showText(aux);
                }
            }
        }

        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.results:
                        toResult();
                        return true;
                    case R.id.friends:
                        toFriends();
                        return true;
                    case R.id.friends_results:
                        toFriendsResults();
                        return true;
                    case R.id.log_out:
                        toLogin();
                        return true;
                    default:
                        showText("Somethings Wrong");
                        return true;
                }
            }
        });

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, myToolbar,
                R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                mDrawerLayout.openDrawer(GravityCompat.START);
                super.onDrawerOpened(drawerView);
            }
        };

        Drawable drawable = ResourcesCompat.getDrawable(getResources(),
                R.drawable.ic_view_headline_white_24dp, this.getTheme());
        mDrawerToggle.setHomeAsUpIndicator(drawable);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        View header = navigationView.getHeaderView(0);
        TextView name = header.findViewById(R.id.header_name);
        TextView email = header.findViewById(R.id.header_email);
        if (user != null) {
            name.setText(user.getDisplayName());
            email.setText(user.getEmail());
        }


        gpsFix = findViewById(R.id.gps_fixed);
        satellite = findViewById(R.id.satellite_in_view);
        satellite2 = findViewById(R.id.satellite_fixed);
        mSatellite = getString(R.string.number_of_satellites);
        mSatelliteFix = getString(R.string.number_of_fixed_satellites);
        String toDisplay;
        String aux2 = getString(R.string.gps_fixed);
        toDisplay = aux2 + " no";
        gpsFix.setText(toDisplay);
        toDisplay = mSatellite + "0";
        satellite.setText(toDisplay);
        toDisplay = mSatelliteFix + "0";
        satellite2.setText(toDisplay);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    mLastKnownLocation = location;
                    putMarker();
                    String aux = getString(R.string.gps_fixed);
                    if (mLastKnownLocation.getSpeed() != 0) {
                        String toDisplay = aux + " yes";
                        gpsFix.setText(toDisplay);
                        isGpsFixed = true;
                    } else {
                        String toDisplay = aux + " no";
                        gpsFix.setText(toDisplay);
                        isGpsFixed = false;
                    }
                }
            }
        };
        GnssStatus.Callback mGnssStatusCallback = new GnssStatus.Callback() {
            @Override
            public void onSatelliteStatusChanged(GnssStatus status) {
                super.onSatelliteStatusChanged(status);
                satelliteCount = status.getSatelliteCount();
                String toDisplay = mSatellite + " " + satelliteCount;
                satellite.setText(toDisplay);
                fixSatelliteCount = 0;
                int i = 0;
                while (i < satelliteCount) {
                    if (status.usedInFix(i)) {
                        fixSatelliteCount++;
                    }
                    i++;
                }

                toDisplay = mSatelliteFix + " " + fixSatelliteCount;
                satellite2.setText(toDisplay);
            }
        };
        LocationManager locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        getLocationPermission();
        try {
            if (mLocationPermissionGranted) {
                if (locManager != null) {
                    locManager.registerGnssStatusCallback(mGnssStatusCallback);
                }
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getDeviceLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
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
            mMarker = mMap.addMarker(aux.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place_black_36dp)));
            b1 = false;
        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {
                return true;
            }
        });
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
                            currentPosition1();
                            putMarker();
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

    public void currentData(View view) {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        Intent i = new Intent(MainActivity.this, CurrentData.class);
        startActivity(i);
    }

    public void startTrack(View view) {
        if (isGpsFixed) {
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
            Intent i = new Intent(MainActivity.this, TrackingActivity.class);
            startActivity(i);
        } else {
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
            Intent i = new Intent(MainActivity.this, NoSignalStart.class);
            startActivity(i);
        }
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

    public void toResult() {
        Intent i = new Intent(MainActivity.this, ResultsListActivity.class);
        startActivity(i);
    }

    public void toLogin() {
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        startActivity(i);
        finish();
    }

    private void toFriends() {
        Intent i = new Intent(MainActivity.this, MyFriends.class);
        startActivity(i);
    }

    private void toFriendsResults() {
        Intent i = new Intent(MainActivity.this, FriendsActivities.class);
        startActivity(i);
    }

    public void currentPosition(View view) {
        currentPosition1();
    }

    public void currentPosition1() {
        int DEFAULT_ZOOM = 16;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mLastKnownLocation.getLatitude(),
                        mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
    }

    private void showText(String text) {
        Snackbar.make(mDrawerLayout, text, Snackbar.LENGTH_LONG).show();
    }
}
