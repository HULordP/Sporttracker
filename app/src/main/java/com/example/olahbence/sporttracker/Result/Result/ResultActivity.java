package com.example.olahbence.sporttracker.Result.Result;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.example.olahbence.sporttracker.R;
import com.example.olahbence.sporttracker.Result.Result.FragmentAdapter.FragmentAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class ResultActivity extends AppCompatActivity {

    private ViewPager pager;
    private PagerAdapter pagerAdapter;

    private File file;
    private TextView time;
    private TextView distance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("Date");

            setContentView(R.layout.result_pager);
            Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(myToolbar);
            getSupportActionBar().setTitle(value);
        }

        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new FragmentAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);

        time = (TextView) findViewById(R.id.time);
        distance = (TextView) findViewById(R.id.distance);

        loadFile();

    }

    private void loadFile() {
        String filePath = getApplicationContext().getFilesDir().getPath() + File.separator + "track.txt";
        file = new File(filePath);
        try {
            FileReader filereader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(filereader);
            String line;
            int i = 0;
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    if (i == 0) {
                        time.setText(line);
                        i++;
                    }
                    if (i == 1) {
                        distance.setText(line);
                        i++;
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

    }

}
