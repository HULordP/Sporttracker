package com.example.olahbence.sporttracker.Result.Result.Fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.olahbence.sporttracker.R;
import com.example.olahbence.sporttracker.Result.Result.ResultAdapter;
import com.example.olahbence.sporttracker.Result.Result.ResultRow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class AveragePacesFragment extends Fragment {

    private ResultAdapter mAdapter;

    private List<ResultRow> input;
    private RecyclerView mRecyclerView;

    public AveragePacesFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result_avaragepaces, container, false);

        bindPaces(view);

        String filePath = getActivity().getApplicationContext().getFilesDir().getPath() + File.separator + "track.txt";
        new loadFileTask().execute(filePath);

        return view;
    }

    private void bindPaces(View root) {
        mRecyclerView = root.findViewById(R.id.avarage_pace_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        input = new ArrayList<>();
        mAdapter = new ResultAdapter(input);
        mRecyclerView.setAdapter(mAdapter);
    }

    private class loadFileTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {

            String filePath = strings[0];
            File file = new File(filePath);
            try

            {
                FileReader filereader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(filereader);
                String line;
                int i = 0;
                try {
                    while ((line = bufferedReader.readLine()) != null) {
                        if (i == 2) {
                            if (line.length() > 0) {
                                String[] averagePace = line.split(",");
                                int size = averagePace.length;
                                for (int ii = 0; ii < size; ii++) {
                                    ResultRow resultRow = new ResultRow(averagePace[ii]);
                                    resultRow.setNumber(Integer.toString(ii + 1) + ". km");
                                    input.add(resultRow);
                                }
                            }
                            if (line.length() == 0) {
                                TextView textView = getActivity().findViewById(R.id.tvSmall);
                                textView.setVisibility(View.VISIBLE);
                                mRecyclerView.setVisibility(View.GONE);
                            }

                        }
                        i++;
                        if (i > 2)
                            break;
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
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAdapter.notifyDataSetChanged();
        }
    }
}
