package com.example.olahbence.sporttracker.Result.Result.Fragments.Post;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.olahbence.sporttracker.R;

import java.util.Date;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private List<PostRow> values;

    PostAdapter(List<PostRow> myDataset) {
        values = myDataset;
    }

    public void add(int position, PostRow item) {
        values.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        values.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.post_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final PostRow postRow = values.get(position);
        holder.message.setText(postRow.getMessage());
        holder.username.setText(postRow.getUsername());
        Date date = new Date(postRow.getDate());
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        String dateToDisplay = DateFormat.format("MM-dd hh:mm a", date).toString();
        holder.date.setText(dateToDisplay);
        String toDisplay = "(" + postRow.getEmail() + ")";
        holder.email.setText(toDisplay);
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView date;
        TextView message;
        TextView email;

        ViewHolder(View v) {
            super(v);
            username = v.findViewById(R.id.username);
            date = v.findViewById(R.id.date);
            message = v.findViewById(R.id.message);
            email = v.findViewById(R.id.email);
        }
    }

}
