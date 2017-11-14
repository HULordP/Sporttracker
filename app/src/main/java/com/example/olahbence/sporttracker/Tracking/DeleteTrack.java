package com.example.olahbence.sporttracker.Tracking;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.olahbence.sporttracker.R;

public class DeleteTrack extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_track);
    }

    public void Delete(View view) {
        setResult(1);
        finish();
    }

    public void Cancel(View view) {
        setResult(0);
        finish();
    }
}
