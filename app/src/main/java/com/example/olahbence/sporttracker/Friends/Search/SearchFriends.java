package com.example.olahbence.sporttracker.Friends.Search;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.olahbence.sporttracker.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SearchFriends extends AppCompatActivity implements FriendsSearchAdapter.OnItemClicked {
    public String TAG;
    private EditText mEditText;
    private List<SearchRow> input;
    private RecyclerView mRecyclerView;
    private FriendsSearchAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String search;
    private ChildEventListener postListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            SearchRow searchRow = dataSnapshot.getValue(SearchRow.class);
            input.add(searchRow);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Search");
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        mEditText = (EditText) findViewById(R.id.friend_email);

        mRecyclerView = (RecyclerView) findViewById(R.id.friends_result_list);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        input = new ArrayList<>();
        mAdapter = new FriendsSearchAdapter(input, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void search(View view) {
        search = mEditText.getText().toString();

        input.clear();
        mAdapter.notifyDataSetChanged();

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mDatabase.getReference("Users");
        myRef.orderByChild("email").startAt(search).endAt(search + "\uff8f").addChildEventListener(postListener);
    }

    @Override
    public void onItemClick(int position) {
        Intent i = new Intent(SearchFriends.this, AddFriends.class);
        String toSend = input.get(position).getEmail();
        i.putExtra("email", toSend);
        startActivity(i);
    }
}
