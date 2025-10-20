package com.example.bloodbankapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bloodbankapp.R;
import com.example.bloodbankapp.models.Donor;
import java.util.List;

public class DonorAdapter extends RecyclerView.Adapter<DonorAdapter.DonorViewHolder> {

    private Context context;
    private List<Donor> donors;

    public DonorAdapter(Context context, List<Donor> donors) {
        this.context = context;
        this.donors = donors;
    }

    @NonNull
    @Override
    public DonorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_donor, parent, false);
        return new DonorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DonorViewHolder holder, int position) {
        Donor donor = donors.get(position);

        holder.tvDonorName.setText(donor.getName());
        holder.tvDonorBloodGroup.setText("Blood Group: " + donor.getBloodGroup());
        holder.tvDonorPhone.setText("Phone: " + donor.getPhone());
        holder.tvDonorAddress.setText("Address: " + donor.getAddress());
    }

    @Override
    public int getItemCount() {
        return donors.size();
    }

    public static class DonorViewHolder extends RecyclerView.ViewHolder {
        TextView tvDonorName, tvDonorBloodGroup, tvDonorPhone, tvDonorAddress;

        public DonorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDonorName = itemView.findViewById(R.id.tvDonorName);
            tvDonorBloodGroup = itemView.findViewById(R.id.tvDonorBloodGroup);
            tvDonorPhone = itemView.findViewById(R.id.tvDonorPhone);
            tvDonorAddress = itemView.findViewById(R.id.tvDonorAddress);
        }
    }
}
