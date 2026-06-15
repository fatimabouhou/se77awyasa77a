package com.mobileproject.se77a.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobileproject.se77a.R;
import com.mobileproject.se77a.database.entities.TimeSlot;

import java.util.List;

public class SlotAdapter extends RecyclerView.Adapter<SlotAdapter.VH> {

    public interface OnSlotClick { void onClick(TimeSlot slot); }

    private final List<TimeSlot> slots;
    private final OnSlotClick    listener;

    public SlotAdapter(List<TimeSlot> slots, OnSlotClick listener) {
        this.slots    = slots;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_slot, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        TimeSlot slot = slots.get(position);
        holder.tvDate.setText(slot.date);
        holder.tvTime.setText(slot.time);
        holder.itemView.setOnClickListener(v -> listener.onClick(slot));
    }

    @Override
    public int getItemCount() { return slots.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvDate, tvTime;
        VH(View v) {
            super(v);
            tvDate = v.findViewById(R.id.tv_slot_date);
            tvTime = v.findViewById(R.id.tv_slot_time);
        }
    }
}