package com.example.olahbence.sporttracker.Friends.MyFriends;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.olahbence.sporttracker.R;

public class DeleteFriends extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_friends);
        TextView tw = findViewById(R.id.del_friends);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String email = extras.getString("Email");
            tw.append("\n" + email + "\nfrom your friends?");
        }
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
