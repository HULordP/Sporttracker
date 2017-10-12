package com.example.olahbence.sporttracker.Result.Result.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.olahbence.sporttracker.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class DistanceFragment extends Fragment implements OnMapReadyCallback {

    private File file;
    private TextView time;
    private TextView distance;
    //    private SupportMapFragment bigMap;
    private GoogleMap mMap;
    private MapView mapView;
    private int DEFAULT_ZOOM = 14;
    private PolylineOptions mPolylineOptions;
    private Polyline mPolyline;
    private boolean polyline_added = false;
    private LatLng prev;
    private boolean map = false;

    public DistanceFragment() {
        /* Required empty public constructor */
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);

        time = (TextView) view.findViewById(R.id.time);
        distance = (TextView) view.findViewById(R.id.distance);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = (MapView) view.findViewById(R.id.mapResult);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

//        bigMap = (SupportMapFragment) getActivity().getSupportFragmentManager()
//                .findFragmentById(R.id.mapResult);
//        bigMap.getMapAsync(this);
        mPolylineOptions = new PolylineOptions().color(Color.BLUE)
                .width(5);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        map = true;

        loadFile();
    }

    private void loadFile() {
        String filePath = getActivity().getApplicationContext().getFilesDir().getPath()
                + File.separator + "track.txt";
        file = new File(filePath);
        try {
            FileReader filereader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(filereader);
            String line;
            int i = 0;
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    if (i == 0) {
                        time.append(" " + line);
                    }
                    if (i == 1) {
                        distance.append(" " + line);
                    }
                    if (i > 2) {
                        try {
                            String[] temp = line.split(",");
                            LatLng temp2 = new LatLng(Double.parseDouble(temp[0])
                                    , Double.parseDouble(temp[1]));
                            if (i == 3)
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        temp2, DEFAULT_ZOOM));
                            if (map) {
                                if (!polyline_added) {
                                    prev = temp2;
                                    polyline_added = true;
                                }
                                mPolylineOptions.add(prev, temp2);
                                mPolylineOptions.visible(true);
                                mPolyline = mMap.addPolyline(mPolylineOptions);
                                prev = temp2;
                            }
                        } catch (Exception e) {
                            Log.e("Exception: %s", e.getMessage());
                        }

                    }
                    i++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            bufferedReader.close();
            filereader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}