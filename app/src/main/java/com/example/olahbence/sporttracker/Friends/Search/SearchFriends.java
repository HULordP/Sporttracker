package com.example.olahbence.sporttracker.Friends.Search;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFriends extends AppCompatActivity {
    public String TAG;
    private EditText mEditText;
    private List<SearchRow> input;
    private RecyclerView mRecyclerView;
    private FriendsSearchAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String email;
    private ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                SearchRow searchRow = childSnapshot.getValue(SearchRow.class);
                if (searchRow.getEmail().equals(email)) {
                    input.add(searchRow);
                    mAdapter.notifyDataSetChanged();
                }
            }
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
        mAdapter = new FriendsSearchAdapter(input);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void search(View view) {
        email = mEditText.getText().toString();

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mDatabase.getReference("Users");
        myRef.addValueEventListener(postListener);
    }
}
