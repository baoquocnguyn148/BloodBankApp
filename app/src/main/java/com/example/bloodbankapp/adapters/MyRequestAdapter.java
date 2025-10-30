package com.example.bloodbankapp.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bloodbankapp.R;
import com.example.bloodbankapp.models.Request;
import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat; // ✅ Import SimpleDateFormat
import java.util.List;
import java.util.Locale; // ✅ Import Locale

public class MyRequestAdapter extends RecyclerView.Adapter<MyRequestAdapter.RequestViewHolder> {

    private final Context context;
    private final List<Request> requestList;
    // Format ngày tháng để hiển thị (chỉ lấy ngày)
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()); // ✅ Định dạng ngày

    public MyRequestAdapter(Context context, List<Request> requestList) {
        this.context = context;
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Request request = requestList.get(position);

        holder.tvPatientAndBlood.setText("Patient: " + request.getPatientName() + " (" + request.getBloodGroup() + ")");
        holder.tvHospitalAndUnits.setText(request.getHospital() + " • " + request.getUnits() + " units");

        // ✅ SỬA LỖI: Sử dụng getRequestTimestamp() và định dạng lại
        if (request.getRequestTimestamp() != null) {
            holder.tvRequestDate.setText("Date: " + dateFormat.format(request.getRequestTimestamp()));
        } else {
            holder.tvRequestDate.setText("Date: N/A");
        }
        // holder.tvRequestDate.setText("Date: " + request.getRequestDate().split(" ")[0]); // ❌ Dòng cũ bị lỗi

        holder.chipStatus.setText(request.getStatus());

        updateStatusChip(holder.chipStatus, request.getStatus());
    }

    private void updateStatusChip(Chip chip, String status) {
        int backgroundColor;
        int textColor = ContextCompat.getColor(context, R.color.white); // Mặc định màu chữ trắng

        // Kiểm tra null cho status
        String currentStatus = (status != null) ? status.toLowerCase() : "pending";

        switch (currentStatus) {
            case "approved":
                backgroundColor = ContextCompat.getColor(context, R.color.chip_approved_bg); // Màu xanh lá
                break;
            case "rejected":
                backgroundColor = ContextCompat.getColor(context, R.color.primary_red); // Màu đỏ
                break;
            default: // "pending"
                backgroundColor = ContextCompat.getColor(context, R.color.chip_pending_bg); // Màu vàng/cam
                break;
        }
        chip.setChipBackgroundColor(ColorStateList.valueOf(backgroundColor));
        chip.setTextColor(textColor);
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView tvPatientAndBlood, tvHospitalAndUnits, tvRequestDate;
        Chip chipStatus;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPatientAndBlood = itemView.findViewById(R.id.tv_patient_name_and_blood);
            tvHospitalAndUnits = itemView.findViewById(R.id.tv_hospital_and_units);
            tvRequestDate = itemView.findViewById(R.id.tv_request_date);
            chipStatus = itemView.findViewById(R.id.chip_status);
        }
    }
}