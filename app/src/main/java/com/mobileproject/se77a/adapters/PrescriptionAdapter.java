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
import com.mobileproject.se77a.database.entities.Prescription;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionAdapter extends RecyclerView.Adapter<PrescriptionAdapter.ViewHolder> {

    public interface OnPrescriptionClickListener {
        void onClick(Prescription prescription);
        void onLongClick(Prescription prescription);
    }

    private List<Prescription>          prescriptions = new ArrayList<>();
    private final OnPrescriptionClickListener listener;

    public PrescriptionAdapter(OnPrescriptionClickListener listener) {
        this.listener = listener;
    }

    public void setPrescriptions(List<Prescription> list) {
        this.prescriptions = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_prescription, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Prescription p = prescriptions.get(position);

        holder.tvDate.setText(p.date);
        holder.tvNotes.setText((p.notes != null && !p.notes.isEmpty()) ? p.notes : "Aucune note");

        // Afficher miniature si le fichier existe
        if (p.imagePath != null) {
            File f = new File(p.imagePath);
            if (f.exists()) {
                holder.ivThumbnail.setImageURI(Uri.fromFile(f));
            } else {
                holder.ivThumbnail.setImageResource(android.R.drawable.ic_menu_camera);
            }
        }

        holder.itemView.setOnClickListener(v -> listener.onClick(p));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onLongClick(p);
            return true;
        });
    }

    @Override
    public int getItemCount() { return prescriptions.size(); }

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