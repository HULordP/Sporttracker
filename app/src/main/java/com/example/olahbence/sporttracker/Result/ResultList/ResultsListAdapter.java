package com.example.olahbence.sporttracker.Result.ResultList;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.olahbence.sporttracker.R;

import java.util.List;

public class ResultsListAdapter extends RecyclerView.Adapter<ResultsListAdapter.ViewHolder> {
    private List<ResultsListRow> values;
    private OnItemClicked onClick;

    ResultsListAdapter(List<ResultsListRow> myDataset, OnItemClicked click) {
        values = myDataset;
        onClick = click;
    }

    public void add(int position, ResultsListRow item) {
        values.add(position, item);
        notifyItemInserted(position);
    }

    @Override
    public ResultsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.resultlist_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ResultsListRow ResultsListRow = values.get(position);
        holder.txtDate.setText(ResultsListRow.getDate());
        holder.txtTime.setText(ResultsListRow.getTime());
        holder.txtDistance.setText(ResultsListRow.getDistance());
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
        TextView txtDate;
        TextView txtTime;
        TextView txtDistance;
        ImageView imgRunning;

        ViewHolder(View v) {
            super(v);
            layout = v;
            txtDate = v.findViewById(R.id.item_date);
            txtTime = v.findViewById(R.id.item_time);
            txtDistance = v.findViewById(R.id.item_distance);
            imgRunning = v.findViewById(R.id.running_icon);
        }
    }

}