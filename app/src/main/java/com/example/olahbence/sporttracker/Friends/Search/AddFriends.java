package com.example.olahbence.sporttracker.Friends.Search;
//TODO ha mar friends ne lehessen hozzaadni !!!!!!!!!!!!

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.olahbence.sporttracker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddFriends extends AppCompatActivity {

    private String IDToAdd;
    private String email;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        TextView tw = (TextView) findViewById(R.id.add_friends);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("email");
            name = extras.getString("name");
            tw.setText(tw.getText() + " " + email + " to your friends?");
            IDToAdd = extras.getString("ID");
        }
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String ID = user.getUid();
        if (ID.equals(IDToAdd)) {
            Button btnAdd = (Button) findViewById(R.id.btnAdd);
            btnAdd.setVisibility(View.GONE);
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
        myRef.child(IDToAdd).child(ID).child("email").setValue(email);
        myRef.child(IDToAdd).child(ID).child("name").setValue(name);
        myRef.child(IDToAdd).child(ID).child("connected").setValue("false");
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
