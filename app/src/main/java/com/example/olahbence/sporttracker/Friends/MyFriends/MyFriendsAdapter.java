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

    public void remove(int position) {
        values.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public MyFriendsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.my_friends_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
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
                onClick.onAddClick(position);
            }
        });
        holder.btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onClearClick(position);
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
        public TextView txtEmail;
        public TextView txtName;
        public ImageButton btnAdd;
        public ImageButton btnClear;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            txtEmail = (TextView) v.findViewById(R.id.friends_result_item_email);
            txtName = (TextView) v.findViewById(R.id.friends_result_item_name);
            btnAdd = (ImageButton) v.findViewById(R.id.add);
            btnClear = (ImageButton) v.findViewById(R.id.delete);
        }
    }

}
