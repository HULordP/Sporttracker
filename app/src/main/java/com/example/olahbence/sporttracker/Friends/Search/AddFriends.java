package com.example.olahbence.sporttracker.Friends.Search;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.olahbence.sporttracker.Friends.MyFriends.MyFriendsRow;
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

public class AddFriends extends AppCompatActivity {

    private String IDToAdd;
    private String TAG;
    private String email;
    private String name;
    private Button btnAdd;
    private TextView tw;
    private List<MyFriendsRow> friends;
    private RelativeLayout rl;
    private ValueEventListener getFriends = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                MyFriendsRow myFriend = childSnapshot.getValue(MyFriendsRow.class);
                friends.add(myFriend);
            }
            int index = friends.size();
            boolean friend_state = false;
            for (int i = 0; i < index; i++) {
                MyFriendsRow myFriendsRow = friends.get(i);
                if (email.equals(myFriendsRow.getEmail())) {
                    tw.setText(R.string.already);
                    btnAdd.setVisibility(View.GONE);
                    friend_state = true;
                    RelativeLayout rl = findViewById(R.id.relative_layout);
                    rl.setVisibility(View.GONE);
                }
            }

            if (!friend_state) {
                tw.append("\n" + email + "\nto your friends?");
                btnAdd.setVisibility(View.VISIBLE);
                rl = findViewById(R.id.relative_layout);
                rl.setVisibility(View.GONE);
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
        setContentView(R.layout.activity_add_friends);
        rl = findViewById(R.id.relative_layout);
        rl.setVisibility(View.VISIBLE);
        tw = findViewById(R.id.add_friends);
        tw.setText("");
        friends = new ArrayList<>();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("email");
            name = extras.getString("name");
            IDToAdd = extras.getString("ID");
        }
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setVisibility(View.GONE);
        String ID = user.getUid();

        if (ID.equals(IDToAdd)) {
            tw.setText(R.string.cant);
            btnAdd.setVisibility(View.GONE);
            rl = findViewById(R.id.relative_layout);
            rl.setVisibility(View.GONE);
        } else {
            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
            DatabaseReference myRef = mDatabase.getReference("Friends");
            myRef.child(ID).addListenerForSingleValueEvent(getFriends);
        }
    }


    public void Add(View view) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String ID = user.getUid();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mDatabase.getReference("Friends");
        myRef.child(ID).child(IDToAdd).child("email").setValue(email);
        myRef.child(ID).child(IDToAdd).child("name").setValue(name);
        myRef.child(ID).child(IDToAdd).child("connected").setValue("true");
        myRef.child(IDToAdd).child(ID).child("email").setValue(user.getEmail());
        myRef.child(IDToAdd).child(ID).child("name").setValue(user.getDisplayName());
        myRef.child(IDToAdd).child(ID).child("connected").setValue("false");
        setResult(1);
        finish();
    }

    public void Cancel(View view) {
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
