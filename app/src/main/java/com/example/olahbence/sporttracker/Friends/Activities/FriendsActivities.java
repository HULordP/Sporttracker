package com.example.olahbence.sporttracker.Friends.Activities;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.olahbence.sporttracker.Friends.MyFriends.UserData;
import com.example.olahbence.sporttracker.R;
import com.example.olahbence.sporttracker.Result.Result.ResultActivity;
import com.example.olahbence.sporttracker.Tracking.Database.Track;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FriendsActivities extends AppCompatActivity implements FriendsActivitiesAdapter.OnItemClicked {

    private RecyclerView mRecyclerView;
    private FriendsActivitiesAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<FriendsActivitiesRow> input;
    private List<UserData> users;
    private List<String> userKeys;
    private List<Track> trackList;
    private List<FriendsActivitiesRow> allTrack;
    private int index = 0;
    private int i = 0;
    private int ii = 0;
    private ValueEventListener getTracks = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                Track track = childSnapshot.getValue(Track.class);
                if (track != null) {
                    track.setKey(childSnapshot.getKey());
                    trackList.add(track);
                    Date date = new Date(track.getDate());
                    String dateToDisplay = DateFormat.format("yyyy-MM-dd hh:mm a",
                            date).toString();
                    double distanceToUpload = Double.parseDouble(track.getDistance());
                    FriendsActivitiesRow current =
                            new FriendsActivitiesRow(users.get(i).getEmail(), users.get(i).getName(),
                                    dateToDisplay, String.format(Locale.ENGLISH, "%.2f",
                                    distanceToUpload) + " km", track.getTime());
                    userKeys.add(users.get(i).getId());
                    allTrack.add(current);
                }
            }
            if (allTrack.size() > 1)
                sortActivities(allTrack);
            Collections.reverse(allTrack);
            Collections.reverse(trackList);
            Collections.reverse(userKeys);
            while (index != 10) {
                if (allTrack.size() == index)
                    break;
                input.add(allTrack.get(index));
                index++;
            }
            mAdapter.notifyDataSetChanged();
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (mLayoutManager.findLastCompletelyVisibleItemPosition() == input.size() - 1) {
                        if (index + 1 != allTrack.size()) {
                            int temp = 5;
                            while (temp != 0) {
                                if (allTrack.size() < 6)
                                    break;
                                if (allTrack.size() == index)
                                    break;
                                if (allTrack.size() == index + 1) {
                                    input.add(allTrack.get(index));
                                    break;
                                }
                                input.add(allTrack.get(index));
                                index++;
                                temp--;
                            }
                            recyclerView.post(new Runnable() {
                                public void run() {
                                    mAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                }
            });
            RelativeLayout rl = findViewById(R.id.relative_layout);
            rl.setVisibility(View.GONE);
            i++;
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            String TAG = "";
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
        }
    };
    private ValueEventListener getConnections = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                UserData ud = childSnapshot.getValue(UserData.class);
                if (ud != null) {
                    ud.setId(childSnapshot.getKey());
                    users.add(ud);
                    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference myRef2 = mDatabase.getReference("Tracks");
                    myRef2.child(users.get(ii).getId()).addListenerForSingleValueEvent(getTracks);
                    ii++;
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            String TAG = "";
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_activities);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Friends Activities");
            ActionBar ab = getSupportActionBar();
            ab.setDisplayHomeAsUpEnabled(true);
        }

        mRecyclerView = findViewById(R.id.friends_result_list);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        RelativeLayout rl = findViewById(R.id.relative_layout);
        rl.setVisibility(View.VISIBLE);

        input = new ArrayList<>();
        allTrack = new ArrayList<>();
        trackList = new ArrayList<>();
        users = new ArrayList<>();
        userKeys = new ArrayList<>();
        mAdapter = new FriendsActivitiesAdapter(input, this);
        mRecyclerView.setAdapter(mAdapter);

        loadActivities();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        RelativeLayout rl = findViewById(R.id.relative_layout);
        rl.setVisibility(View.GONE);
    }

    private void loadActivities() {
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference myRef = mDatabase.getReference("Connections");
        if (user != null)
            myRef.child(user.getUid()).addListenerForSingleValueEvent(getConnections);
    }

    private void sortActivities(List<FriendsActivitiesRow> a) {
        int aux = a.size();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
        for (int temp = 0; temp < aux - 1; temp++)
            for (int k = temp + 1; k < aux; k++) {
                Date before = new Date();
                Date after = new Date();
                try {
                    before = format.parse(a.get(temp).getDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                try {
                    after = format.parse(a.get(k).getDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (before.after(after)) {
                    FriendsActivitiesRow t = a.get(temp);
                    a.set(temp, a.get(k));
                    a.set(k, t);
                    Track tt = trackList.get(temp);
                    trackList.set(temp, trackList.get(k));
                    trackList.set(k, tt);
                    String ttt = userKeys.get(temp);
                    userKeys.set(temp, userKeys.get(k));
                    userKeys.set(k, ttt);
                }
            }
    }

    @Override
    public void onItemClick(final int position) {
        RelativeLayout rl = findViewById(R.id.relative_layout);
        rl.setVisibility(View.VISIBLE);
        Track track = trackList.get(position);
        String filename = track.getFilename();
        Date date = new Date(track.getDate());
        final String toSend = DateFormat.format("yyyy-MM-dd hh:mm a", date).toString();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference trackTxtRef = storageRef.child("Tracks" + File.separator + filename + ".txt");

        String filePath = getApplicationContext().getFilesDir().getPath() + File.separator + "track.txt";
        File downloadedFile = new File(filePath);
        trackTxtRef.getFile(downloadedFile).addOnSuccessListener
                (new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Intent i = new Intent(FriendsActivities.this, ResultActivity.class);
                        i.putExtra("Friend", true);
                        i.putExtra("Date", toSend);
                        i.putExtra("Name", input.get(position).getName());
                        i.putExtra("Email", input.get(position).getEmail());
                        i.putExtra("Identity", "FriendsActivities");
                        i.putExtra("Key", trackList.get(position).getKey());
                        i.putExtra("UserKey", userKeys.get(position));
                        startActivity(i);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
    }

}
