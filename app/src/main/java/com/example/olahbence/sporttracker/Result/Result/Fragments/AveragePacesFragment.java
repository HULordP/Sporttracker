package com.example.olahbence.sporttracker.Result.Result.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.olahbence.sporttracker.R;
import com.example.olahbence.sporttracker.Result.Result.ResultAdapter;
import com.example.olahbence.sporttracker.Result.Result.ResultRow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AveragePacesFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<ResultRow> input;

    private File file;

    public AveragePacesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        input = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result_avaragepaces, container, false);

        bindPaces(view);

        loadPaces();

        return view;
    }

    private void bindPaces(View root) {
        mRecyclerView = (RecyclerView) root.findViewById(R.id.avarage_pace_list);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(mAdapter);
    }

    private void loadPaces() {

        String filePath = getActivity().getApplicationContext().getFilesDir().getPath() + File.separator + "track.txt";
        file = new

                File(filePath);
        try

        {
            FileReader filereader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(filereader);
            String line;
            int i = 0;
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    if (i == 0) {
                        i++;
                    }
                    if (i == 1) {
                        i++;
                    }
                    if (i == 2) {
                        String[] averagePace = line.split(",");
                        int size = averagePace.length;
                        for (int ii = 0; ii < size; ii++) {
                            ResultRow resultRow = new ResultRow(line);
                            input.add(resultRow);
                            mAdapter = new ResultAdapter(input);
                            mAdapter.notifyDataSetChanged();
                        }
                        i++;

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            bufferedReader.close();
            filereader.close();
        } catch (
                Exception e)

        {
            e.printStackTrace();
        }
    }
}
