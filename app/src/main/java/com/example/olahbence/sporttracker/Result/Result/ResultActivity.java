package com.example.olahbence.sporttracker.Result.Result;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.olahbence.sporttracker.R;
import com.example.olahbence.sporttracker.Result.Result.FragmentAdapter.FragmentAdapter;

import java.io.File;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("Date");
            setContentView(R.layout.result_pager);
            Toolbar myToolbar = findViewById(R.id.toolbar);
            setSupportActionBar(myToolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(value);
                ActionBar ab = getSupportActionBar();
                ab.setDisplayHomeAsUpEnabled(true);
            }
        }

        ViewPager pager = findViewById(R.id.pager);
        PagerAdapter pagerAdapter = new FragmentAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);

        pagerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        String filePath = getApplicationContext().getFilesDir().getPath() + File.separator + "track.txt";
        File toDelete = new File(filePath);
        boolean b = toDelete.delete();
    }

}
