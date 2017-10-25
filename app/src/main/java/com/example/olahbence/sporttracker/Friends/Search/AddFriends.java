package com.example.olahbence.sporttracker.Friends.Search;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.olahbence.sporttracker.R;

public class AddFriends extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        TextView tw = (TextView) findViewById(R.id.add_friends);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String email = extras.getString("email");
            tw.setText(tw.getText() + " " + email + " to your friends?");
        }
    }

    public void Add(View view) {
    }

    public void Cancel(View view) {
    }
}
