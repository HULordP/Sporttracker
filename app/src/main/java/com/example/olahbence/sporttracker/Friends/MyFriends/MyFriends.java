package com.example.olahbence.sporttracker.Friends.MyFriends;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.olahbence.sporttracker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyFriends extends AppCompatActivity implements MyFriendsAdapter.OnItemClicked {

    private String TAG;
    private List<MyFriendsRow> input;
    private RecyclerView mRecyclerView;
    private MyFriendsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<String> IDs;
    private UserDatas myFriend;
    private int aux;

    private ValueEventListener getData = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            myFriend = dataSnapshot.getValue(UserDatas.class);
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            String ID = user.getUid();
            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
            DatabaseReference myRef = mDatabase.getReference("Connections");
            myRef.child(ID).child(IDs.get(aux)).child("email").setValue(myFriend.getEmail());
            myRef.child(ID).child(IDs.get(aux)).child("name").setValue(myFriend.getName());
            myRef.child(IDs.get(aux)).child(ID).child("email").setValue(user.getEmail());
            myRef.child(IDs.get(aux)).child(ID).child("name").setValue(user.getDisplayName());
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
        }
    };

    private ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                MyFriendsRow myFriend = childSnapshot.getValue(MyFriendsRow.class);
                input.add(myFriend);
                mAdapter.notifyDataSetChanged();
                IDs.add(childSnapshot.getKey());
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
        setContentView(R.layout.activity_my_friends);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("My friends");
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.friends_result_list);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        input = new ArrayList<>();
        IDs = new ArrayList<>();
        mAdapter = new MyFriendsAdapter(input, this);
        mRecyclerView.setAdapter(mAdapter);

        loadFriends();
    }

    private void loadFriends() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String ID = user.getUid();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mDatabase.getReference("Friends");
        myRef.child(ID).addValueEventListener(postListener);
    }

    @Override
    public void onAddClick(int position) {
        aux = position;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String ID = user.getUid();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mDatabase.getReference("Friends");
        myRef.child(ID).child(IDs.get(position)).child("connected").setValue("true");
        DatabaseReference myRef2 = mDatabase.getReference("Users");
        myRef2.child(IDs.get(position)).addValueEventListener(getData);
        input.clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClearClick(int position) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String ID = user.getUid();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mDatabase.getReference("Friends");
        myRef.child(ID).child(IDs.get(position)).removeValue();
        myRef.child(IDs.get(position)).child(ID).removeValue();
        input.clear();
        mAdapter.notifyDataSetChanged();
    }
}
