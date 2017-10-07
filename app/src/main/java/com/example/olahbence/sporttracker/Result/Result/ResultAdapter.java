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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtAveragePace;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            txtAveragePace = (TextView) v.findViewById(R.id.item_avaragepace);

        }
    }

    public void add(int position, ResultRow item) {
        values.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        values.remove(position);
        notifyItemRemoved(position);
    }

    public ResultAdapter(List<ResultRow> myDataset) {
        values = myDataset;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.resultlist_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final ResultRow resultRow = values.get(position);
        holder.txtAveragePace.setText(resultRow.getAveragePace());
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

}
