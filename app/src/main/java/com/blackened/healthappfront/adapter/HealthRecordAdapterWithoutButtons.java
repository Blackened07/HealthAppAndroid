package com.blackened.healthappfront.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blackened.healthappfront.R;
import com.blackened.healthappfront.healthRecord.HealthRecordResponseDTO;

import java.util.ArrayList;
import java.util.List;

public class HealthRecordAdapterWithoutButtons extends RecyclerView.Adapter<HealthRecordAdapterWithoutButtons.ViewHolder>{

    private List<HealthRecordResponseDTO> records = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    public void setRecords(List<HealthRecordResponseDTO> records) {
        this.records = records;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_health_record_without_buttons, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HealthRecordResponseDTO record = records.get(position);

        holder.tvDate.setText(record.getDisplayDate());
        holder.tvType.setText(record.getDisplayType());
        holder.tvValue.setText(record.getDisplayValue());

    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvType, tvValue;

        ViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date_item);
            tvType = itemView.findViewById(R.id.tv_type);
            tvValue = itemView.findViewById(R.id.tv_value);
        }
    }
}
