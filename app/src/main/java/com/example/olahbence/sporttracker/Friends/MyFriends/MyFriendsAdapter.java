package com.example.olahbence.sporttracker.Friends.MyFriends;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.olahbence.sporttracker.R;

import java.util.List;

public class MyFriendsAdapter extends RecyclerView.Adapter<MyFriendsAdapter.ViewHolder> {
    private List<MyFriendsRow> values;
    private OnItemClicked onClick;

    public MyFriendsAdapter(List<MyFriendsRow> myDataset, OnItemClicked click) {
        values = myDataset;
        onClick = click;
    }

    public void add(int position, MyFriendsRow item) {
        values.add(position, item);
        notifyItemInserted(position);
    }

    @Override
    public MyFriendsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.my_friends_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final MyFriendsRow resultRow = values.get(position);
        holder.txtEmail.setText(resultRow.getEmail());
        holder.txtName.setText(resultRow.getName());
        if (resultRow.getConnected().equals("true"))
            holder.btnAdd.setVisibility(View.GONE);
        else
            holder.btnAdd.setVisibility(View.VISIBLE);
        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onAddClick(holder.getAdapterPosition());
            }
        });
        holder.btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onClearClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public interface OnItemClicked {
        void onAddClick(int position);

        void onClearClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View layout;
        public TextView txtEmail;
        public TextView txtName;
        public ImageButton btnAdd;
        public ImageButton btnClear;

        ViewHolder(View v) {
            super(v);
            layout = v;
            txtEmail = v.findViewById(R.id.friends_result_item_email);
            txtName = v.findViewById(R.id.friends_result_item_name);
            btnAdd = v.findViewById(R.id.add);
            btnClear = v.findViewById(R.id.delete);
        }
    }

}
