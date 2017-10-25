package com.example.olahbence.sporttracker.Friends;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.olahbence.sporttracker.Friends.Search.SearchFriends;
import com.example.olahbence.sporttracker.R;

public class FriendsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_activity);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Search");
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    public void toSearch(View view) {
        Intent i = new Intent(FriendsActivity.this, SearchFriends.class);
        startActivity(i);
    }

    public void toFriendsActivities(View view) {
    }

    public void toMyFriends(View view) {
    }
}
