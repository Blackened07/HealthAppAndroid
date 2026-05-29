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
import com.blackened.healthappfront.user.UserResponseDTO;

import java.util.ArrayList;
import java.util.List;

public class FamilyMemberAdapter extends RecyclerView.Adapter<FamilyMemberAdapter.ViewHolder> {
    private List<UserResponseDTO> members = new ArrayList<>();
    private OnMemberListener listener;


    public interface OnMemberListener {
        void onMemberClick(long targetId, String targetName, String targetRole);
    }

    public void setOnMemberListener(OnMemberListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setMembers(List<UserResponseDTO> members) {
        this.members = members;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_family_member, parent, false);
        return new FamilyMemberAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserResponseDTO member = members.get(position);

        holder.tvName.setText(member.getFirstName());


        holder.btn_inspect.setOnClickListener(v -> {
            listener.onMemberClick(member.getId(), member.getFirstName(), member.getFamilyRole());
        });
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageButton btn_inspect;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_member_name);
            btn_inspect = itemView.findViewById(R.id.btn_inspect);
        }
    }

}
