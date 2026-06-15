package com.mobileproject.se77a.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobileproject.se77a.R;

import java.util.List;

public class StringAdapter extends RecyclerView.Adapter<StringAdapter.VH> {

    public interface OnItemClick { void onClick(String value); }

    private final List<String> items;
    private final OnItemClick  listener;

    public StringAdapter(List<String> items, OnItemClick listener) {
        this.items    = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_string, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        String item = items.get(position);
        holder.tv.setText(item);
        holder.itemView.setOnClickListener(v -> listener.onClick(item));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tv;
        VH(View v) {
            super(v);
            tv = v.findViewById(R.id.tv_item);
        }
    }
}