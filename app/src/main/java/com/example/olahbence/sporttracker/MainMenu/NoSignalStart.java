package com.example.olahbence.sporttracker.MainMenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.olahbence.sporttracker.R;
import com.example.olahbence.sporttracker.Tracking.TrackingActivity;

public class NoSignalStart extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_signal_start);
    }


    public void Cancel(View view) {
        finish();
    }

    public void Start(View view) {
        Intent i = new Intent(NoSignalStart.this, TrackingActivity.class);
        startActivity(i);
        finish();
    }
}
