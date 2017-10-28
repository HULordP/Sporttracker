package com.example.olahbence.sporttracker.MainMenu;
//TODO remove ValueEventListeners!!!!!!!!!!!!!!

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.olahbence.sporttracker.Friends.FriendsActivity;
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
    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;


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
        mPlanetTitles = getResources().getStringArray(R.array.navigation_drawer);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        mPlanetTitles[0] = user.getDisplayName();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item,
                mPlanetTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.drawer_open,
                R.string.drawer_close
        );
        Drawable drawable = ResourcesCompat.getDrawable(getResources(),
                R.drawable.ic_view_headline_black_24dp, this.getTheme());
        mDrawerToggle.setHomeAsUpIndicator(drawable);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        gpsFix = (TextView) findViewById(R.id.gps_fixed);
        satellite = (TextView) findViewById(R.id.satellite_in_view);
        satellite2 = (TextView) findViewById(R.id.satellite_fixed);
        mSatellite = getString(R.string.number_of_satellites);
        mSatelliteFix = getString(R.string.number_of_fixed_satellites);
        String toDisplay;
        String aux = getString(R.string.gps_fixed);
        toDisplay = aux + " no";
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
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        Intent i = new Intent(MainActivity.this, CurrentData.class);
        startActivity(i);
    }

    public void startTrack(View view) {
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

    public void toResult() {
        Intent i = new Intent(MainActivity.this, ResultsListActivity.class);
        startActivity(i);
    }

    public void toLogin() {
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    private void selectItem(int position) {
        if (position == 1)
            toResult();
        if (position == 2)
            toFriends();
        if (position == 3) {
            FirebaseAuth.getInstance().signOut();
            toLogin();
        }
        mDrawerList.setItemChecked(position, true);
        setTitle(mPlanetTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void toFriends() {
        Intent i = new Intent(MainActivity.this, FriendsActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
}
