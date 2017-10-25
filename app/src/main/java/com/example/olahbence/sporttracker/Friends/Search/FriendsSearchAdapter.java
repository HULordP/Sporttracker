package com.example.olahbence.sporttracker.Friends.Search;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.olahbence.sporttracker.R;

import java.util.List;

public class FriendsSearchAdapter extends RecyclerView.Adapter<FriendsSearchAdapter.ViewHolder> {
    private List<SearchRow> values;
    private OnItemClicked onClick;

    public FriendsSearchAdapter(List<SearchRow> myDataset, OnItemClicked click) {
        values = myDataset;
        onClick = click;
    }

    public void add(int position, SearchRow item) {
        values.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        values.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public FriendsSearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.friends_search_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final SearchRow resultRow = values.get(position);
        holder.txtEmail.setText(resultRow.getEmail());
        holder.txtName.setText(resultRow.getName());
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
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            txtEmail = (TextView) v.findViewById(R.id.friends_result_item_email);
            txtName = (TextView) v.findViewById(R.id.friends_result_item_name);
        }
    }

}
