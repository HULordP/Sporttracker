package com.example.olahbence.sporttracker.Result.Result.Fragments.AveragePaces;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.olahbence.sporttracker.R;

import java.util.List;

public class AveragePaceAdapter extends RecyclerView.Adapter<AveragePaceAdapter.ViewHolder> {
    private List<AveragePaceRow> values;

    public AveragePaceAdapter(List<AveragePaceRow> myDataset) {
        values = myDataset;
    }

    public void add(int position, AveragePaceRow item) {
        values.add(position, item);
        notifyItemInserted(position);
    }

    @Override
    public AveragePaceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.average_pace_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final AveragePaceRow averagePaceRow = values.get(position);
        holder.txtNumber.setText(averagePaceRow.getNumber());
        holder.txtAveragePace.setText(averagePaceRow.getAveragePace());
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtAveragePace;
        public TextView txtNumber;

        ViewHolder(View v) {
            super(v);
            txtNumber = v.findViewById(R.id.item_number);
            txtAveragePace = v.findViewById(R.id.item_avaragepace);
        }
    }

}
