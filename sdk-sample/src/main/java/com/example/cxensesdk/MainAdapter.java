package com.example.cxensesdk;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    private List<String> data;
    private ItemClickListener clickListener;

    public MainAdapter(String[] data, ItemClickListener clickListener) {
        this.data = Arrays.asList(data);
        this.clickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String item = data.get(position);
        holder.title.setText(item);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    interface ItemClickListener {
        void onItemClick(String item);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && clickListener != null) {
                    clickListener.onItemClick(data.get(position));
                }
            });
        }
    }
}
