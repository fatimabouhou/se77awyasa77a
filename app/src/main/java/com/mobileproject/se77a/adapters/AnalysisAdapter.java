package com.mobileproject.se77a.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobileproject.se77a.R;
import com.mobileproject.se77a.database.entities.Analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AnalysisAdapter extends RecyclerView.Adapter<AnalysisAdapter.ViewHolder> {

    public interface OnAnalysisClickListener {
        void onClick(Analysis analysis);
        void onLongClick(Analysis analysis);
    }

    private List<Analysis>              analyses = new ArrayList<>();
    private final OnAnalysisClickListener listener;

    public AnalysisAdapter(OnAnalysisClickListener listener) {
        this.listener = listener;
    }

    public void setAnalyses(List<Analysis> list) {
        this.analyses = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Réutilise le même item layout que les ordonnances
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_prescription, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Analysis a = analyses.get(position);

        holder.tvDate.setText(a.date);
        holder.tvNotes.setText((a.notes != null && !a.notes.isEmpty()) ? a.notes : "Aucune note");

        if (a.imagePath != null) {
            File f = new File(a.imagePath);
            if (f.exists()) {
                holder.ivThumbnail.setImageURI(Uri.fromFile(f));
            } else {
                holder.ivThumbnail.setImageResource(android.R.drawable.ic_menu_camera);
            }
        }

        holder.itemView.setOnClickListener(v -> listener.onClick(a));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onLongClick(a);
            return true;
        });
    }

    @Override
    public int getItemCount() { return analyses.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail;
        TextView  tvDate, tvNotes;

        ViewHolder(View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.iv_prescription_thumb);
            tvDate      = itemView.findViewById(R.id.tv_prescription_date);
            tvNotes     = itemView.findViewById(R.id.tv_prescription_notes);
        }
    }
}