package com.blackened.healthappfront.healthRecord;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blackened.healthappfront.R;

import java.util.ArrayList;
import java.util.List;

public class HealthRecordAdapter extends RecyclerView.Adapter<HealthRecordAdapter.ViewHolder> {

    private List<HealthRecordResponseDTO> records = new ArrayList<>();
    private OnDeleteClickListener deleteListener;
    private OnEditClickListener editClickListener;
    private OnNoteClickListener noteClickListener;

    public interface OnDeleteClickListener {
        void onDelete(HealthRecordResponseDTO record);
    }
    public interface OnEditClickListener {
        void onEdit(HealthRecordResponseDTO record);
    }
    public interface OnNoteClickListener {
        void onNote(HealthRecordResponseDTO record);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteListener = listener;
    }

    public void setOnEditClickListener(OnEditClickListener listener) {
        this.editClickListener = listener;
    }
    public void setOnNoteClickListener(OnNoteClickListener listener) {
        this.noteClickListener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setRecords(List<HealthRecordResponseDTO> records) {
        this.records = records;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_health_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HealthRecordResponseDTO record = records.get(position);

        holder.tvType.setText(record.getDisplayType());
        holder.tvValue.setText(record.getDisplayValue());

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(record);
            }
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (editClickListener != null) {
                editClickListener.onEdit(record);
            }
        });

        holder.btnNote.setOnClickListener(v -> {
            if (noteClickListener != null) {
                noteClickListener.onNote(record);
            }
        });

       /* if (record.getNote() != null && !record.getNote().isEmpty()) {
            holder.tvNote.setText(record.getNote());
            holder.tvNote.setVisibility(View.VISIBLE);
        } else {
            holder.tvNote.setVisibility(View.GONE);
        }*/

    /*    holder.btnEdit.setOnClickListener(v -> listener.onEdit(record));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(record));*/
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvValue;
        ImageButton btnNote, btnDelete, btnEdit;

        ViewHolder(View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tv_type);
            tvValue = itemView.findViewById(R.id.tv_value);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnNote = itemView.findViewById(R.id.btn_note);
            btnEdit = itemView.findViewById(R.id.btn_edit);
        }
    }
}
