package com.example.bloodbankapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bloodbankapp.R;
import com.example.bloodbankapp.database.DatabaseHelper;
import com.example.bloodbankapp.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboard extends AppCompatActivity {

    private TextView tvWelcomeAdmin, tvDonorsCount, tvRecipientsCount, tvRequestsCount, tvBloodBanksCount;
    private Button btnLogout;
    private MaterialToolbar toolbar;
    private Button btnManageUsers, btnViewRequests, btnManageInventory, btnViewAnalytics;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            goToLoginActivity();
            return;
        }

        initViews();
        setupToolbar();
        setupClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        btnLogout = findViewById(R.id.btn_logout);
        tvWelcomeAdmin = findViewById(R.id.tv_welcome_admin);

        // Ánh xạ các TextView thống kê
        tvDonorsCount = findViewById(R.id.tv_donor_count);
        tvRecipientsCount = findViewById(R.id.tv_recipient_count);

        tvRequestsCount = findViewById(R.id.tv_request_count);
        tvBloodBanksCount = findViewById(R.id.tv_blood_bank_count);

        // Ánh xạ các nút quản lý
        btnManageUsers = findViewById(R.id.btn_manage_users);
        btnViewRequests = findViewById(R.id.btn_view_requests);
        btnManageInventory = findViewById(R.id.btn_manage_inventory);
        btnViewAnalytics = findViewById(R.id.btn_view_analytics);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Admin Dashboard");
        }
    }

    private void setupClickListeners() {
        btnLogout.setOnClickListener(v -> showLogoutConfirmationDialog());

        btnManageUsers.setOnClickListener(v -> startActivity(new Intent(this, ManageUsersActivity.class)));
        btnViewRequests.setOnClickListener(v -> startActivity(new Intent(this, ViewRequestsActivity.class)));
        btnManageInventory.setOnClickListener(v -> startActivity(new Intent(this, ManageInventoryActivity.class)));
        btnViewAnalytics.setOnClickListener(v -> startActivity(new Intent(this, AnalyticsActivity.class)));
    }

    private void loadDashboardData() {
        String adminName = sessionManager.getUserName();
        tvWelcomeAdmin.setText("Welcome, " + (adminName != null ? adminName : "Admin"));

        // 🔍 DEBUG: Log all users in database
        Log.d("AdminDashboard", "=== DEBUG: ALL USERS IN DATABASE ===");
        java.util.List<com.example.bloodbankapp.models.User> allUsers = dbHelper.getAllUsers();
        Log.d("AdminDashboard", "Total users in DB: " + allUsers.size());
        for (com.example.bloodbankapp.models.User user : allUsers) {
            Log.d("AdminDashboard", "User: " + user.getName() + " | Email: " + user.getEmail() + " | Role: [" + user.getRole() + "]");
        }
        Log.d("AdminDashboard", "=== END DEBUG ===");

        // Gọi các hàm đếm từ DatabaseHelper
        int donorCount = dbHelper.countUsersByRole("donor");
        int recipientCount = dbHelper.countUsersByRole("recipient");
        int bloodBankCount = dbHelper.countUsersByRole("blood_bank_staff");
        int requestCount = dbHelper.countAllRequests(); // Giả định hàm này đã được thêm vào DatabaseHelper

        // Cập nhật các TextView
        tvDonorsCount.setText(String.valueOf(donorCount));
        tvRecipientsCount.setText(String.valueOf(recipientCount));
        tvBloodBanksCount.setText(String.valueOf(bloodBankCount));
        tvRequestsCount.setText(String.valueOf(requestCount));

        Log.d("AdminDashboard", "Stats Loaded: Donors=" + donorCount + ", Recipients=" + recipientCount + ", Staff=" + bloodBankCount + ", Requests=" + requestCount);
    }

    private void showLogoutConfirmationDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Confirm Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Log Out", (dialog, which) -> performLogout())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performLogout() {
        FirebaseAuth.getInstance().signOut();
        sessionManager.logoutUser();
        goToLoginActivity();
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(AdminDashboard.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
