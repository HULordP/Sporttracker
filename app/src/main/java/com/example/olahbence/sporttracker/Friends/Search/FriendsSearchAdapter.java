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

    @Override
    public FriendsSearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.friends_search_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final SearchRow resultRow = values.get(position);
        holder.txtEmail.setText(resultRow.getEmail());
        holder.txtName.setText(resultRow.getName());
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
        public TextView txtEmail;
        public TextView txtName;

        ViewHolder(View v) {
            super(v);
            layout = v;
            txtEmail = v.findViewById(R.id.friends_result_item_email);
            txtName = v.findViewById(R.id.friends_result_item_name);
        }
    }

}
