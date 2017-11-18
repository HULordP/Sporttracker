package com.example.olahbence.sporttracker.Friends.Activities;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.olahbence.sporttracker.R;

import java.util.List;

public class FriendsActivitiesAdapter extends RecyclerView.Adapter<FriendsActivitiesAdapter.ViewHolder> {
    private List<FriendsActivitiesRow> values;
    private OnItemClicked onClick;

    FriendsActivitiesAdapter(List<FriendsActivitiesRow> myDataSet, OnItemClicked click) {
        values = myDataSet;
        onClick = click;
    }

    public void add(int position, FriendsActivitiesRow item) {
        values.add(position, item);
        notifyItemInserted(position);
    }

    @Override
    public FriendsActivitiesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                  int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.friends_activities_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final FriendsActivitiesRow friendsActivitiesRow = values.get(position);
        holder.txtEmail.setText(friendsActivitiesRow.getEmail());
        holder.txtName.setText(friendsActivitiesRow.getName());
        holder.txtDate.setText(friendsActivitiesRow.getDate());
        holder.txtTime.setText(friendsActivitiesRow.getTime());
        holder.txtDistance.setText(friendsActivitiesRow.getDistance());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(holder.getAdapterPosition());
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
        public View layout;
        TextView txtEmail;
        TextView txtName;
        TextView txtDate;
        TextView txtTime;
        TextView txtDistance;

        ViewHolder(View v) {
            super(v);
            layout = v;
            txtEmail = v.findViewById(R.id.item_email);
            txtName = v.findViewById(R.id.item_name);
            txtDate = v.findViewById(R.id.item_date);
            txtTime = v.findViewById(R.id.item_time);
            txtDistance = v.findViewById(R.id.item_distance);
        }
    }

}
