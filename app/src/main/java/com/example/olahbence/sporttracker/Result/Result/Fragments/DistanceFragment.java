package com.example.olahbence.sporttracker.Result.Result.Fragments;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
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
import java.util.ArrayList;

public class DistanceFragment extends Fragment implements OnMapReadyCallback {

    private TextView time;
    private TextView distance;
    private TextView name;
    private TextView email;
    private GoogleMap mMap;
    private PolylineOptions mPolylineOptions;
    private boolean polyline_added = false;
    private boolean helper = false;
    private LatLng prev;
    private String toEmail;
    private String toName;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            toEmail = extras.getString("Email");
            toName = extras.getString("Name");
            String identity = extras.getString("Identity");
            if (identity != null) {
                if (!identity.equals("ResultListActivity"))
                    helper = true;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = new View(this.getContext());
        if (helper) {
            view = inflater.inflate(R.layout.fragment_result_friend, container, false);
            time = view.findViewById(R.id.time);
            distance = view.findViewById(R.id.distance);
            name = view.findViewById(R.id.name);
            email = view.findViewById(R.id.email);
            RelativeLayout rl = view.findViewById(R.id.relative_layout);
            rl.setVisibility(View.VISIBLE);
        }
        if (!helper) {
            view = inflater.inflate(R.layout.fragment_result, container, false);
            time = view.findViewById(R.id.time);
            distance = view.findViewById(R.id.distance);
            RelativeLayout rl = view.findViewById(R.id.relative_layout);
            rl.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        if (helper) {
            email.setText(toEmail);
            name.setText(toName);
        }

        MapView mapView = view.findViewById(R.id.mapResult);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
        mPolylineOptions = new PolylineOptions().color(Color.BLUE)
                .width(5);
        String filePath = getActivity().getApplicationContext().getFilesDir().getPath()
                + File.separator + "track.txt";
        new loadFileTask().execute(filePath);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private class loadFileTask extends AsyncTask<String, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            String filePath = strings[0];
            File file = new File(filePath);
            ArrayList<String> result = new ArrayList<String>();
            try {
                FileReader filereader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(filereader);
                String line;
                int i = 0;
                try {
                    while ((line = bufferedReader.readLine()) != null) {
                        if (i == 0) {
                            result.add(0, line);
                        }
                        if (i == 1) {
                            result.add(1, line);
                        }
                        if (i > 2) {
                            try {
                                if (i == 3)
                                    result.add(2, line);
                                String[] temp = line.split(",");
                                if (!temp[0].isEmpty()) {
                                    LatLng temp2 = new LatLng(Double.parseDouble(temp[0])
                                            , Double.parseDouble(temp[1]));
                                    if (!polyline_added) {
                                        prev = temp2;
                                        polyline_added = true;
                                    }
                                    mPolylineOptions.add(prev, temp2);
                                    prev = temp2;
                                }
                                i++;
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
            return result;
        }


        @Override
        protected void onPostExecute(ArrayList<String> result) {
            super.onPostExecute(result);
            time.append("\n" + result.get(0));
            if (Double.parseDouble(result.get(1)) > 0.1) {
                distance.append("\n" + result.get(1) + " km");
            } else {
                distance.append("\n" + "0 km");
            }
            if (result.size() > 2) {
                String[] temp = result.get(2).split(",");
                LatLng camera = new LatLng(Double.parseDouble(temp[0])
                        , Double.parseDouble(temp[1]));
                int DEFAULT_ZOOM = 14;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        camera, DEFAULT_ZOOM));
            }
            mPolylineOptions.visible(true);
            Polyline mPolyline = mMap.addPolyline(mPolylineOptions);
            RelativeLayout rl = getActivity().findViewById(R.id.relative_layout);
            rl.setVisibility(View.GONE);
        }
    }
}