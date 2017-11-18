package com.example.olahbence.sporttracker.Result.ResultList;

import android.content.Intent;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ResultsListActivity extends AppCompatActivity implements ResultsListAdapter.OnItemClicked {

    public String TAG;
    private RecyclerView mRecyclerView;
    private ResultsListAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<ResultsListRow> input;
    private List<ResultsListRow> allTrack;
    private List<Track> trackList;
    private int index;
    private ValueEventListener trackListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                Track track = childSnapshot.getValue(Track.class);
                if (track != null) {
                    track.setKey(childSnapshot.getKey());
                    trackList.add(track);
                    Date date = new Date(track.getDate());
                    String dateToDisplay = DateFormat.format("yyyy-MM-dd hh:mm a", date).toString();
                    double distanceToUpload = Double.parseDouble(track.getDistance());
                    ResultsListRow current =
                            new ResultsListRow(dateToDisplay, String.format(Locale.ENGLISH,
                                    "%.2f", distanceToUpload) + " km", track.getTime());
                    allTrack.add(current);
                }
                Collections.reverse(allTrack);
                Collections.reverse(trackList);
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
        setContentView(R.layout.activity_results_list);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Results");
            ActionBar ab = getSupportActionBar();
            ab.setDisplayHomeAsUpEnabled(true);
        }

        mRecyclerView = findViewById(R.id.result_list);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        trackList = new ArrayList<>();

        RelativeLayout rl = findViewById(R.id.relative_layout);
        rl.setVisibility(View.VISIBLE);

        input = new ArrayList<>();
        allTrack = new ArrayList<>();
        mAdapter = new ResultsListAdapter(input, this);
        mRecyclerView.setAdapter(mAdapter);

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser fUser = mAuth.getCurrentUser();
        if (fUser != null) {
            DatabaseReference myRef = mDatabase.getReference("Tracks").child(fUser.getUid());
            myRef.addListenerForSingleValueEvent(trackListener);
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        RelativeLayout rl = findViewById(R.id.relative_layout);
        rl.setVisibility(View.GONE);
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
                        Intent i = new Intent(ResultsListActivity.this, ResultActivity.class);
                        i.putExtra("Date", toSend);
                        i.putExtra("Key", trackList.get(position).getKey());
                        i.putExtra("Identity", "ResultListActivity");
                        startActivity(i);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
    }

}
