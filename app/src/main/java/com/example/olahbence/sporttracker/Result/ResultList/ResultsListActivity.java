package com.example.olahbence.sporttracker.Result.ResultList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.example.olahbence.sporttracker.R;
import com.example.olahbence.sporttracker.Result.Result.ResultActivity;
import com.example.olahbence.sporttracker.Tracking.Database.Track;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ResultsListActivity extends AppCompatActivity implements ResultsListAdapter.OnItemClicked {

    public String TAG;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<ResultsListRow> input;
    private List<Track> trackList;
    private File downloadedFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_list);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Results");
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.result_list);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        trackList = new ArrayList<>();

        input = new ArrayList<>();
        mAdapter = new ResultsListAdapter(input, this);
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder
                            target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        input.remove(viewHolder.getAdapterPosition());
                        mAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                    }
                };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);


        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser fUser = mAuth.getCurrentUser();
        DatabaseReference myRef = mDatabase.getReference("Tracks").child(fUser.getUid());
        myRef.addValueEventListener(postListener);

    }

    @Override
    public void onItemClick(int position) {
        Track track = trackList.get(position);
        String filename = track.getFilename();
        Date date = new Date(track.getDate());
        String toSend = date.toString();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference trackTxtRef = storageRef.child("Tracks" + File.separator + filename + ".txt");

        String filePath = getApplicationContext().getFilesDir().getPath() + File.separator + "track.txt";
        downloadedFile = new File(filePath);
        trackTxtRef.getFile(downloadedFile);

        Intent i = new Intent(ResultsListActivity.this, ResultActivity.class);
        i.putExtra("Date", toSend);
        startActivity(i);
    }


    ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Track track = new Track();
            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                track = childSnapshot.getValue(Track.class);
                trackList.add(track);
            }
            Date date = new Date(track.getDate());
            String dateToDisplay = DateFormat.getDateInstance(DateFormat.MEDIUM).format(date)
                    + DateFormat.getDateInstance(DateFormat.SHORT).format(date);
            double distanceToUpload = Double.parseDouble(track.getDistance());
            ResultsListRow current = new ResultsListRow(dateToDisplay, String.format("%.2f", distanceToUpload) + " km", track.getTime());
            input.add(current);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
        }
    };

}
