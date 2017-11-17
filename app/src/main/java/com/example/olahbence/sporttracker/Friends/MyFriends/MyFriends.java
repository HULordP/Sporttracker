package com.example.olahbence.sporttracker.Friends.MyFriends;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.olahbence.sporttracker.Friends.Search.SearchFriends;
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
    private List<String> IDs;
    private int aux;
    private ValueEventListener getFriends = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            input.clear();
            IDs.clear();
            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                MyFriendsRow myFriend = childSnapshot.getValue(MyFriendsRow.class);
                input.add(myFriend);
                IDs.add(childSnapshot.getKey());
                mAdapter.notifyDataSetChanged();
            }
            RelativeLayout rl = findViewById(R.id.relative_layout);
            rl.setVisibility(View.GONE);
            mRecyclerView = findViewById(R.id.friends_result_list);
            mRecyclerView.setVisibility(View.VISIBLE);
            FloatingActionButton fab = findViewById(R.id.addButton);
            fab.setVisibility(View.VISIBLE);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
        }
    };
    private ValueEventListener getData = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            UserDatas myFriend = dataSnapshot.getValue(UserDatas.class);
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            String ID = user.getUid();
            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
            DatabaseReference myRef = mDatabase.getReference("Connections");
            myRef.child(ID).child(IDs.get(aux)).child("email").setValue(myFriend.getEmail());
            myRef.child(ID).child(IDs.get(aux)).child("name").setValue(myFriend.getName());
            myRef.child(IDs.get(aux)).child(ID).child("email").setValue(user.getEmail());
            myRef.child(IDs.get(aux)).child(ID).child("name").setValue(user.getDisplayName());
            input.clear();
            loadFriends();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
        }
    };
    private int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friends);
        mRecyclerView = findViewById(R.id.friends_result_list);
        mRecyclerView.setVisibility(View.GONE);
        FloatingActionButton fab = findViewById(R.id.addButton);
        fab.setVisibility(View.GONE);
        RelativeLayout rl = findViewById(R.id.relative_layout);
        rl.setVisibility(View.VISIBLE);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("My friends");
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SearchFriends.class);
                startActivity(i);
            }
        });

        input = new ArrayList<>();
        IDs = new ArrayList<>();
        mAdapter = new MyFriendsAdapter(input, this);
        mRecyclerView.setAdapter(mAdapter);

        loadFriends();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void loadFriends() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String ID = user.getUid();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mDatabase.getReference("Friends");
        myRef.child(ID).addListenerForSingleValueEvent(getFriends);
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
        myRef2.child(IDs.get(position)).addListenerForSingleValueEvent(getData);
        showText("Adding was successful");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            String ID = user.getUid();
            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
            DatabaseReference myRef = mDatabase.getReference("Friends");
            myRef.child(ID).child(IDs.get(pos)).removeValue();
            myRef.child(IDs.get(pos)).child(ID).removeValue();
            DatabaseReference myRef2 = mDatabase.getReference("Connections");
            myRef2.child(ID).child(IDs.get(pos)).removeValue();
            myRef2.child(IDs.get(pos)).child(ID).removeValue();
            input.remove(pos);
            IDs.remove(pos);
            mAdapter.notifyDataSetChanged();
            showText("Deleting was successful");
        }
    }

    @Override
    public void onClearClick(int position) {
        pos = position;
        Intent i = new Intent(this, DeleteFriends.class);
        String email = input.get(position).getEmail();
        i.putExtra("Email", email);
        startActivityForResult(i, 0);
    }

    private void showText(String text) {
        LinearLayout linearLayout = findViewById(R.id.lin_lay);
        Snackbar.make(linearLayout, text, Snackbar.LENGTH_LONG).show();
    }

}
