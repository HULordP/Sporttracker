package com.example.olahbence.sporttracker.Friends.Activities;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.olahbence.sporttracker.R;

import java.util.List;

public class FriendsActivitesAdapter extends RecyclerView.Adapter<FriendsActivitesAdapter.ViewHolder> {
    private List<FriendsActivitiesRow> values;
    private OnItemClicked onClick;

    public FriendsActivitesAdapter(List<FriendsActivitiesRow> myDataset, OnItemClicked click) {
        values = myDataset;
        onClick = click;
    }

    public void add(int position, FriendsActivitiesRow item) {
        values.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        values.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public FriendsActivitesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.friends_activities_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final FriendsActivitiesRow friendsActivitiesRow = values.get(position);
        holder.txtEmail.setText(friendsActivitiesRow.getEmail());
        holder.txtName.setText(friendsActivitiesRow.getName());
        holder.txtDate.setText(friendsActivitiesRow.getmDate());
        holder.txtTime.setText(friendsActivitiesRow.getmTime());
        holder.txtDistance.setText(friendsActivitiesRow.getmDistance());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public interface OnItemClicked {
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtEmail;
        public TextView txtName;
        public TextView txtDate;
        public TextView txtTime;
        public TextView txtDistance;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            txtEmail = (TextView) v.findViewById(R.id.item_email);
            txtName = (TextView) v.findViewById(R.id.item_name);
            txtDate = (TextView) v.findViewById(R.id.item_date);
            txtTime = (TextView) v.findViewById(R.id.item_time);
            txtDistance = (TextView) v.findViewById(R.id.item_distance);
        }
    }

}
