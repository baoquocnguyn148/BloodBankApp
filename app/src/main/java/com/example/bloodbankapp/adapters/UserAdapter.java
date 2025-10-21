package com.example.bloodbankapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout; // ✅ 1. Import LinearLayout
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloodbankapp.R;
import com.example.bloodbankapp.activities.EditProfileActivity;
import com.example.bloodbankapp.database.DatabaseHelper;
import com.example.bloodbankapp.models.User;
import com.example.bloodbankapp.utils.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final Context context;
    private final List<User> userList;
    private final DatabaseHelper dbHelper;
    private final String currentUserRole;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
        this.dbHelper = new DatabaseHelper(context);

        SessionManager sessionManager = new SessionManager(context);
        this.currentUserRole = sessionManager.getUserRole();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        // Hiển thị thông tin người dùng (không đổi)
        holder.tvUserId.setText("ID: " + user.getUserId());
        holder.tvUserName.setText("Name: " + (user.getName() != null ? user.getName() : "N/A"));
        holder.tvUserEmail.setText("Email: " + (user.getEmail() != null ? user.getEmail() : "N/A"));
        holder.tvUserPhone.setText("Phone: " + (user.getPhone() != null && !user.getPhone().isEmpty() ? user.getPhone() : "N/A"));
        holder.tvUserRole.setText("Role: " + (user.getRole() != null ? user.getRole() : "N/A"));
        holder.tvUserBlood.setText("Blood: " + (user.getBloodGroup() != null && !user.getBloodGroup().isEmpty() ? user.getBloodGroup() : "N/A"));

        if ("admin".equalsIgnoreCase(currentUserRole)) {

            holder.adminActionsLayout.setVisibility(View.VISIBLE);

            if ("admin".equalsIgnoreCase(user.getRole())) {
                holder.btnDelete.setEnabled(false);
                holder.btnDelete.setText("Cannot Delete");
            } else {
                holder.btnDelete.setEnabled(true);
                holder.btnDelete.setText("Delete");
            }
        } else {

            holder.adminActionsLayout.setVisibility(View.GONE);
        }

        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditProfileActivity.class);
            intent.putExtra("USER_ID_TO_EDIT", user.getUserId());
            context.startActivity(intent);
        });


        holder.btnDelete.setOnClickListener(v -> {
            if ("admin".equalsIgnoreCase(user.getRole())) {
                Toast.makeText(context, "Cannot delete an admin account.", Toast.LENGTH_SHORT).show();
                return;
            }
            showDeleteConfirmationDialog(user, position);
        });
    }

    private void showDeleteConfirmationDialog(User user, int position) {
        new MaterialAlertDialogBuilder(context)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete user '" + user.getName() + "'? This action cannot be undone.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteUser(user.getUserId());
                    userList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, userList.size());
                    Toast.makeText(context, user.getName() + " has been deleted.", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    // ViewHolder class
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserId, tvUserName, tvUserEmail, tvUserPhone, tvUserRole, tvUserBlood;
        Button btnEdit, btnDelete;
        LinearLayout adminActionsLayout;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserId = itemView.findViewById(R.id.tv_user_id);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvUserEmail = itemView.findViewById(R.id.tv_user_email);
            tvUserPhone = itemView.findViewById(R.id.tv_user_phone);
            tvUserRole = itemView.findViewById(R.id.tv_user_role);
            tvUserBlood = itemView.findViewById(R.id.tv_user_blood);

            adminActionsLayout = itemView.findViewById(R.id.layout_admin_actions);
            btnEdit = itemView.findViewById(R.id.btn_edit_user);
            btnDelete = itemView.findViewById(R.id.btn_delete_user);
        }
    }
}
