package com.example.bloodbankapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloodbankapp.R;
import com.example.bloodbankapp.models.BloodUnit;

import java.util.List;

/**
 * Adapter chỉ để hiển thị thông tin kho máu cho Recipient (chỉ đọc)
 * Không có chức năng edit/delete
 */
public class BloodAvailabilityAdapter extends RecyclerView.Adapter<BloodAvailabilityAdapter.BloodAvailabilityViewHolder> {

    private final Context context;
    private final List<BloodUnit> bloodUnitList;

    public BloodAvailabilityAdapter(Context context, List<BloodUnit> bloodUnitList) {
        this.context = context;
        this.bloodUnitList = bloodUnitList;
    }

    @NonNull
    @Override
    public BloodAvailabilityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_blood_availability, parent, false);
        return new BloodAvailabilityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BloodAvailabilityViewHolder holder, int position) {
        BloodUnit unit = bloodUnitList.get(position);

        holder.tvBloodGroup.setText("Blood Group: " + unit.getBloodGroup());
        holder.tvUnits.setText("Units: " + unit.getUnits());
        holder.tvExpiryDate.setText("Expires: " + unit.getExpiryDate());
    }

    @Override
    public int getItemCount() {
        return bloodUnitList.size();
    }

    public static class BloodAvailabilityViewHolder extends RecyclerView.ViewHolder {
        TextView tvBloodGroup, tvUnits, tvExpiryDate;

        public BloodAvailabilityViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBloodGroup = itemView.findViewById(R.id.tv_blood_group);
            tvUnits = itemView.findViewById(R.id.tv_units);
            tvExpiryDate = itemView.findViewById(R.id.tv_expiry_date);
        }
    }
}


