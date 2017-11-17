package com.example.olahbence.sporttracker.Friends.Search;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.olahbence.sporttracker.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

public class SearchFriends extends AppCompatActivity implements FriendsSearchAdapter.OnItemClicked {
    public String TAG;
    private EditText mEditText;
    private List<SearchRow> input;
    private RecyclerView mRecyclerView;
    private FriendsSearchAdapter mAdapter;
    private List<String> IDs;
    private boolean thereIsResult = false;
    private AVLoadingIndicatorView avi;
    private ChildEventListener searchFriend = new ChildEventListener() {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            SearchRow searchRow = dataSnapshot.getValue(SearchRow.class);
            input.add(searchRow);
            mAdapter.notifyDataSetChanged();
            IDs.add(dataSnapshot.getKey());
            avi.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            thereIsResult = true;
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
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Search");
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        mEditText = findViewById(R.id.friend_email);

        mRecyclerView = findViewById(R.id.friends_result_list);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        IDs = new ArrayList<>();
        input = new ArrayList<>();
        mAdapter = new FriendsSearchAdapter(input, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void search(View view) {
        final TextView tvNoResult = findViewById(R.id.tvNoResult);
        tvNoResult.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        avi = findViewById(R.id.avi);
        avi.setVisibility(View.VISIBLE);


        String search = mEditText.getText().toString();

        input.clear();
        IDs.clear();
        mAdapter.notifyDataSetChanged();

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = mDatabase.getReference("Users");
        myRef.orderByChild("email").startAt(search).endAt(search + "\uff8f").addChildEventListener(searchFriend);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!thereIsResult) {
                    avi.setVisibility(View.GONE);
                    tvNoResult.setVisibility(View.VISIBLE);
                }
                myRef.removeEventListener(searchFriend);
            }
        }, 10000);
    }

    @Override
    public void onItemClick(int position) {
        Intent i = new Intent(SearchFriends.this, AddFriends.class);
        String toSend = input.get(position).getEmail();
        i.putExtra("email", toSend);
        String toSend2 = input.get(position).getName();
        i.putExtra("name", toSend2);
        String ID = IDs.get(position);
        i.putExtra("ID", ID);
        startActivityForResult(i, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            LinearLayout linearLayout = findViewById(R.id.lin_lay);
            Snackbar.make(linearLayout, "Adding was successful", Snackbar.LENGTH_LONG).show();
        }
    }

}
