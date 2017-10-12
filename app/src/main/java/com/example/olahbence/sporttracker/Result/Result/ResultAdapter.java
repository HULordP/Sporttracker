package com.example.olahbence.sporttracker.Result.Result;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.olahbence.sporttracker.R;

import java.util.List;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {
    private List<ResultRow> values;

    public ResultAdapter(List<ResultRow> myDataset) {
        values = myDataset;
    }

    public void add(int position, ResultRow item) {
        values.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        values.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public ResultAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.result_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final ResultRow resultRow = values.get(position);
        holder.txtNumber.setText(resultRow.getNumber());
        holder.txtAveragePace.setText(resultRow.getAveragePace());
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtAveragePace;
        public TextView txtNumber;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            txtNumber = (TextView) v.findViewById(R.id.item_number);
            txtAveragePace = (TextView) v.findViewById(R.id.item_avaragepace);
        }
    }

}
