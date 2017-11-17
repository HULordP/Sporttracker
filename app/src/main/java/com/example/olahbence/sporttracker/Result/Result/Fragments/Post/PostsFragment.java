package com.example.olahbence.sporttracker.Result.Result.Fragments.Post;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.olahbence.sporttracker.R;
import com.example.olahbence.sporttracker.Tracking.Database.Track;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static android.content.ContentValues.TAG;

public class PostsFragment extends Fragment {

    private TextView twNoPosts;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView posts;
    private ImageButton btnSend;
    private EditText etPost;
    private DatabaseReference myRef;
    private DatabaseReference myRef2;
    private DatabaseReference myRef3;
    private String trackKey;
    private String userKey;
    private boolean fromResult;
    private List<PostRow> input;
    private PostAdapter postAdapter;
    private ValueEventListener getMessages = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            input.clear();
            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                PostRow postRow = childSnapshot.getValue(PostRow.class);
                input.add(postRow);
            }
            Collections.reverse(input);
            postAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
        }
    };
    private ValueEventListener getPostID = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Track track = dataSnapshot.getValue(Track.class);
            if (track.getPostID() != null) {
                FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
                myRef2 = mDatabase.getReference("Posts").child(track.getPostID());
                myRef2.addValueEventListener(getMessages);
                twNoPosts.setVisibility(View.GONE);
            } else {
                twNoPosts.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
        }
    };
    private ValueEventListener sendMessage = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Track track = dataSnapshot.getValue(Track.class);
            if (track.getPostID() == null) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser fUser = mAuth.getCurrentUser();
                FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
                DatabaseReference ref2 = mDatabase.getReference("Posts").push();
                String pushId = ref2.getKey();
                String pushId2 = ref2.child(pushId).push().getKey();
                ref2.child(pushId2).child("username").setValue(fUser.getDisplayName());
                ref2.child(pushId2).child("message").setValue(etPost.getText().toString());
                ref2.child(pushId2).child("date").setValue(Calendar.getInstance().getTimeInMillis());
                ref2.child(pushId2).child("email").setValue(fUser.getEmail());
                myRef.child("postID").setValue(pushId);
            } else {
                String postId = track.getPostID();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser fUser = mAuth.getCurrentUser();
                FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
                DatabaseReference ref2 = mDatabase.getReference("Posts").child(postId);
                String pushId = ref2.push().getKey();
                ref2.child(pushId).child("username").setValue(fUser.getDisplayName());
                ref2.child(pushId).child("message").setValue(etPost.getText().toString());
                ref2.child(pushId).child("date").setValue(Calendar.getInstance().getTimeInMillis());
                ref2.child(pushId).child("email").setValue(fUser.getEmail());
            }
            etPost.setText("");
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
        }
    };

    public PostsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            String identity = extras.getString("Identity");
            if (identity.equals("FriendsActivities")) {
                trackKey = extras.getString("Key");
                userKey = extras.getString("UserKey");
                fromResult = false;
            } else {
                trackKey = extras.getString("Key");
                fromResult = true;
            }
        }
        input = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_posts, container, false);

        twNoPosts = view.findViewById(R.id.tvNo);
        posts = view.findViewById(R.id.posts);
        btnSend = view.findViewById(R.id.sendPost);
        etPost = view.findViewById(R.id.etPost);
        mLayoutManager = new LinearLayoutManager(getContext());
        posts.setLayoutManager(mLayoutManager);
        postAdapter = new PostAdapter(input);
        posts.setAdapter(postAdapter);

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        if (fromResult) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser fUser = mAuth.getCurrentUser();
            myRef = mDatabase.getReference("Tracks").child(fUser.getUid()).child(trackKey);
            myRef.addValueEventListener(getPostID);
        } else {
            myRef = mDatabase.getReference("Tracks").child(userKey).child(trackKey);
            myRef.addValueEventListener(getPostID);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
                if (fromResult) {
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser fUser = mAuth.getCurrentUser();
                    myRef = mDatabase.getReference("Tracks").child(fUser.getUid()).child(trackKey);
                    myRef.addListenerForSingleValueEvent(sendMessage);
                } else {
                    myRef = mDatabase.getReference("Tracks").child(userKey).child(trackKey);
                    myRef.addListenerForSingleValueEvent(sendMessage);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myRef2.removeEventListener(getMessages);
        myRef.removeEventListener(getPostID);
    }
}
