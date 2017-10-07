package com.example.olahbence.sporttracker.Result.Result.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.olahbence.sporttracker.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class DistanceFragment extends Fragment {

    private File file;
    private TextView time;
    private TextView distance;

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

        loadFile();

        return view;
    }

    private void loadFile() {
        String filePath = getActivity().getApplicationContext().getFilesDir().getPath() + File.separator + "track.txt";
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
                    if(i==2)
                        break;
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