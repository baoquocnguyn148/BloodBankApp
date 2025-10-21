package com.example.bloodbankapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloodbankapp.R;

import com.example.bloodbankapp.database.DatabaseHelper;
import com.example.bloodbankapp.models.Request;

import java.util.List;

public class BloodRequestAdapter extends RecyclerView.Adapter<BloodRequestAdapter.RequestViewHolder> {

    private final List<Request> requestList;
    private final Context context;

    private final DatabaseHelper dbHelper;

    public BloodRequestAdapter(Context context, List<Request> requestList) {
        this.context = context;
        this.requestList = requestList;
        this.dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blood_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Request currentRequest = requestList.get(position);

        // Hiển thị thông tin
        holder.tvPatientName.setText("Patient: " + currentRequest.getPatientName());
        holder.tvHospitalName.setText("At: " + currentRequest.getHospital());
        // Sửa lại cho đúng tên getter là getUnits()
        holder.tvBloodType.setText("Required: " + currentRequest.getUnits() + " units of " + currentRequest.getBloodGroup());
        holder.tvRequestDate.setText("Date: " + currentRequest.getRequestDate());

        // --- Logic hiển thị trạng thái và nút bấm ---
        holder.tvStatus.setText(currentRequest.getStatus());

        switch (currentRequest.getStatus()) {
            case "Approved":
                holder.tvStatus.setBackgroundColor(Color.parseColor("#4CAF50")); // Màu xanh lá
                holder.layoutActions.setVisibility(View.GONE);
                break;
            case "Rejected":
                holder.tvStatus.setBackgroundColor(Color.parseColor("#F44336")); // Màu đỏ
                holder.layoutActions.setVisibility(View.GONE);
                break;
            default: // Trạng thái "Pending"
                holder.tvStatus.setBackgroundColor(Color.parseColor("#FF9800")); // Màu cam
                holder.layoutActions.setVisibility(View.VISIBLE);
                break;
        }

        holder.btnApprove.setOnClickListener(v -> {
            updateStatus(currentRequest, "Approved", holder.getAdapterPosition());
        });

        holder.btnReject.setOnClickListener(v -> {
            updateStatus(currentRequest, "Rejected", holder.getAdapterPosition());
        });
    }

    private void updateStatus(Request request, String newStatus, int position) {
        int result = dbHelper.updateRequestStatus(request.getId(), newStatus);

        if (result > 0) {
            request.setStatus(newStatus);
            notifyItemChanged(position); // Cập nhật lại item view
            Toast.makeText(context, "Request has been " + newStatus, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView tvBloodType, tvPatientName, tvHospitalName, tvRequestDate;
        TextView tvStatus;
        Button btnApprove, btnReject;
        LinearLayout layoutActions;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBloodType = itemView.findViewById(R.id.tv_blood_type);
            tvPatientName = itemView.findViewById(R.id.tv_patient_name);
            tvHospitalName = itemView.findViewById(R.id.tv_hospital_name);
            tvRequestDate = itemView.findViewById(R.id.tv_request_date);
            tvStatus = itemView.findViewById(R.id.tv_status);
            btnApprove = itemView.findViewById(R.id.btn_approve);
            btnReject = itemView.findViewById(R.id.btn_reject);
            layoutActions = itemView.findViewById(R.id.layout_actions);
        }
    }
}
