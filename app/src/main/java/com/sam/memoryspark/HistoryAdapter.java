package com.sam.memoryspark;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<History> historyItems;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(History item);
    }

    public HistoryAdapter(List<History> items, OnItemClickListener listener) {
        this.historyItems = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_view_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        History item = historyItems.get(position);
        Log.d("HistoryAdapter", "Memuat item: " + item.getDeckName() + ", " + item.getNilai() + ", " + item.getTanggal());
        holder.tvDeckName.setText(item.getDeckName());
        holder.tvTanggal.setText(item.getTanggal());
        holder.tvPersentase.setText(item.getNilai());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDeckName, tvTanggal, tvPersentase;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDeckName = itemView.findViewById(R.id.rowVDeckName);
            tvTanggal = itemView.findViewById(R.id.rowVTanggal);
            tvPersentase = itemView.findViewById(R.id.rowVNilai);
        }
    }
}