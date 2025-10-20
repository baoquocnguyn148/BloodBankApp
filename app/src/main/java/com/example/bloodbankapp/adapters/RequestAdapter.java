package com.example.bloodbankapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloodbankapp.R;
import com.example.bloodbankapp.database.DatabaseHelper;
import com.example.bloodbankapp.models.Request;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    private List<Request> requestList;
    private final Context context;
    private final DatabaseHelper dbHelper;
    private final boolean showAdminButtons; // ✅ Biến mới để kiểm soát việc hiển thị nút

    // ✅ Constructor mới linh hoạt hơn
    public RequestAdapter(Context context, List<Request> requestList, boolean showAdminButtons) {
        this.context = context;
        this.requestList = requestList != null ? requestList : new ArrayList<>();
        this.dbHelper = new DatabaseHelper(context);
        this.showAdminButtons = showAdminButtons;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Sử dụng một layout chung cho request, ví dụ item_request_card.xml
        // Bạn cần tạo file này nếu chưa có, hoặc dùng file item_request.xml đã có
        View view = LayoutInflater.from(context).inflate(R.layout.item_request_card, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Request request = requestList.get(position);
        holder.bind(request, showAdminButtons);
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    // ✅ Hàm mới để cập nhật dữ liệu cho adapter
    public void updateData(List<Request> newRequests) {
        this.requestList.clear();
        this.requestList.addAll(newRequests);
        notifyDataSetChanged();
    }

    class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView tvPatientName, tvHospital, tvBloodInfo;
        Chip chipStatus; // Dùng Chip cho đẹp
        View adminButtonsLayout; // Layout chứa 2 nút approve/reject
        Button btnApprove, btnReject;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các view từ item_request_card.xml
            tvPatientName = itemView.findViewById(R.id.tv_patient_name);
            tvHospital = itemView.findViewById(R.id.tv_hospital);
            tvBloodInfo = itemView.findViewById(R.id.tv_blood_info);
            chipStatus = itemView.findViewById(R.id.chip_status);
            adminButtonsLayout = itemView.findViewById(R.id.layout_admin_buttons);
            btnApprove = itemView.findViewById(R.id.btn_approve);
            btnReject = itemView.findViewById(R.id.btn_reject);
        }

        void bind(final Request request, boolean showAdminButtons) {
            tvPatientName.setText(request.getPatientName());
            tvHospital.setText(request.getHospital());
            tvBloodInfo.setText(request.getUnits() + " units of " + request.getBloodGroup());
            chipStatus.setText(request.getStatus());

            // Đổi màu chip tùy theo trạng thái
            switch (request.getStatus().toLowerCase()) {
                case "approved":
                    chipStatus.setChipBackgroundColorResource(R.color.status_approved_bg);
                    chipStatus.setTextColor(ContextCompat.getColor(context, R.color.status_approved_text));
                    break;
                case "rejected":
                    chipStatus.setChipBackgroundColorResource(R.color.status_rejected_bg);
                    chipStatus.setTextColor(ContextCompat.getColor(context, R.color.status_rejected_text));
                    break;
                default: // Pending
                    chipStatus.setChipBackgroundColorResource(R.color.status_pending_bg);
                    chipStatus.setTextColor(ContextCompat.getColor(context, R.color.status_pending_text));
                    break;
            }

            // Ẩn/hiện các nút Approve/Reject
            if (showAdminButtons && "Pending".equals(request.getStatus())) {
                adminButtonsLayout.setVisibility(View.VISIBLE);
            } else {
                adminButtonsLayout.setVisibility(View.GONE);
            }

            btnApprove.setOnClickListener(v -> updateRequestStatus(request, "Approved", getAdapterPosition()));
            btnReject.setOnClickListener(v -> updateRequestStatus(request, "Rejected", getAdapterPosition()));
        }

        private void updateRequestStatus(Request request, String newStatus, int position) {
            int result = dbHelper.updateRequestStatus(request.getId(), newStatus);
            if (result > 0) {
                request.setStatus(newStatus);
                notifyItemChanged(position);
                Toast.makeText(context, "Request " + newStatus.toLowerCase(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
