package com.example.bloodbankapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bloodbankapp.R;
import com.example.bloodbankapp.models.Donation; // Bạn sẽ tạo model này
import com.google.android.material.chip.Chip;

import java.util.List;

public class DonationHistoryAdapter extends RecyclerView.Adapter<DonationHistoryAdapter.DonationViewHolder> {

    private List<Donation> donationList;

    public DonationHistoryAdapter(List<Donation> donationList) {
        this.donationList = donationList;
    }

    @NonNull
    @Override
    public DonationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donation_history, parent, false);
        return new DonationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DonationViewHolder holder, int position) {
        Donation donation = donationList.get(position);
        holder.bind(donation);
    }

    @Override
    public int getItemCount() {
        return donationList.size();
    }

    static class DonationViewHolder extends RecyclerView.ViewHolder {
        TextView tvBankName, tvUnits, tvDate;
        Chip chipStatus;

        public DonationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBankName = itemView.findViewById(R.id.tv_blood_bank_name);
            tvUnits = itemView.findViewById(R.id.tv_units_donated);
            tvDate = itemView.findViewById(R.id.tv_donation_date);
            chipStatus = itemView.findViewById(R.id.chip_status);
        }

        public void bind(Donation donation) {
            tvBankName.setText(donation.getBankName());
            tvUnits.setText(donation.getUnits() + " unit(s) donated");
            tvDate.setText(donation.getDate());
            chipStatus.setText("✓ " + donation.getStatus());
        }
    }
}
