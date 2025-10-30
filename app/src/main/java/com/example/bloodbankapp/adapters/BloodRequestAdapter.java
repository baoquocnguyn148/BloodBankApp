package com.example.bloodbankapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
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
// import com.example.bloodbankapp.database.DatabaseHelper; // ❌ Không dùng SQLite nữa
import com.example.bloodbankapp.models.Request;
import com.google.firebase.firestore.FirebaseFirestore; // ✅ Import Firestore

import java.text.SimpleDateFormat; // ✅ Import SimpleDateFormat
import java.util.List;
import java.util.Locale; // ✅ Import Locale

public class BloodRequestAdapter extends RecyclerView.Adapter<BloodRequestAdapter.RequestViewHolder> {

    private final List<Request> requestList;
    private final Context context;
    // private final DatabaseHelper dbHelper; // ❌
    private final FirebaseFirestore dbFirestore; // ✅

    private static final String TAG = "BloodRequestAdapter";

    // Format ngày tháng để hiển thị
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public BloodRequestAdapter(Context context, List<Request> requestList) {
        this.context = context;
        this.requestList = requestList;
        // this.dbHelper = new DatabaseHelper(context); // ❌
        this.dbFirestore = FirebaseFirestore.getInstance(); // ✅
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

        // --- Hiển thị thông tin ---
        holder.tvPatientName.setText("Patient: " + (currentRequest.getPatientName() != null ? currentRequest.getPatientName() : "N/A"));
        holder.tvHospitalName.setText("At: " + (currentRequest.getHospital() != null ? currentRequest.getHospital() : "N/A"));
        holder.tvBloodType.setText("Required: " + currentRequest.getUnits() + " units of " + (currentRequest.getBloodGroup() != null ? currentRequest.getBloodGroup() : "N/A"));

        // Hiển thị ngày tháng từ Timestamp
        if (currentRequest.getRequestTimestamp() != null) {
            holder.tvRequestDate.setText("Date: " + dateFormat.format(currentRequest.getRequestTimestamp()));
        } else {
            holder.tvRequestDate.setText("Date: N/A");
        }

        // --- Logic hiển thị trạng thái và nút bấm ---
        String status = currentRequest.getStatus() != null ? currentRequest.getStatus().toLowerCase() : "pending"; // Mặc định là pending nếu null
        holder.tvStatus.setText(currentRequest.getStatus()); // Hiển thị text gốc

        switch (status) {
            case "approved":
                holder.tvStatus.setBackgroundColor(Color.parseColor("#4CAF50")); // Green
                holder.layoutActions.setVisibility(View.GONE);
                break;
            case "rejected":
                holder.tvStatus.setBackgroundColor(Color.parseColor("#F44336")); // Red
                holder.layoutActions.setVisibility(View.GONE);
                break;
            default: // "pending" or other
                holder.tvStatus.setBackgroundColor(Color.parseColor("#FF9800")); // Orange
                holder.layoutActions.setVisibility(View.VISIBLE);
                break;
        }

        // Lấy Firestore document ID
        final String documentId = currentRequest.getDocumentId(); // ✅ Sử dụng getter mới

        // --- Cập nhật logic sự kiện nút ---
        holder.btnApprove.setOnClickListener(v -> {
            if (documentId != null && !documentId.isEmpty()) {
                updateStatusInFirestore(documentId, "Approved", holder.getAdapterPosition()); // ✅ Gọi hàm cập nhật Firestore
            } else {
                Log.e(TAG, "Document ID is null or empty at position " + holder.getAdapterPosition());
                Toast.makeText(context, "Error: Cannot update status without ID.", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnReject.setOnClickListener(v -> {
            if (documentId != null && !documentId.isEmpty()) {
                updateStatusInFirestore(documentId, "Rejected", holder.getAdapterPosition()); // ✅ Gọi hàm cập nhật Firestore
            } else {
                Log.e(TAG, "Document ID is null or empty at position " + holder.getAdapterPosition());
                Toast.makeText(context, "Error: Cannot update status without ID.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ✅ Hàm cập nhật status trên Firestore (không thay đổi so với lần trước)
    private void updateStatusInFirestore(String documentId, String newStatus, int position) {
        dbFirestore.collection("requests").document(documentId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Request status updated successfully for document: " + documentId);
                    // Cập nhật trạng thái trong list cục bộ và thông báo cho adapter
                    if (position >= 0 && position < requestList.size()) { // Kiểm tra index hợp lệ
                        requestList.get(position).setStatus(newStatus);
                        notifyItemChanged(position); // Chỉ cập nhật item thay đổi
                    }
                    Toast.makeText(context, "Request has been " + newStatus, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error updating request status for document: " + documentId, e);
                    Toast.makeText(context, "Failed to update status. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    // ViewHolder class (giữ nguyên)
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