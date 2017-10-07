package com.example.olahbence.sporttracker.Login;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.olahbence.sporttracker.R;

public class VerifyEmail extends AppCompatActivity{

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_email);
    }

    public void toLogin(View view){
        finish();
    }

}
