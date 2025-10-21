package com.example.bloodbankapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bloodbankapp.R;
import com.example.bloodbankapp.database.DatabaseHelper;
import com.example.bloodbankapp.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Map;

public class StaffDashboardActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextView tvStaffWelcome, tvTotalUnits, tvPendingRequests;
    private MaterialCardView cardManageInventory, cardHandleRequests, cardViewDonors, cardLogout;

    private SessionManager sessionManager;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_dashboard);

        sessionManager = new SessionManager(this);
        dbHelper = new DatabaseHelper(this);

        if (!sessionManager.isLoggedIn() || !"blood_bank_staff".equals(sessionManager.getUserRole())) {
            logout();
            return;
        }

        initViews();
        setupToolbar();
        setupWelcomeMessage();
        setupClickListeners();
        loadDashboardData();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvStaffWelcome = findViewById(R.id.tv_staff_welcome);
        tvTotalUnits = findViewById(R.id.tv_total_units);
        tvPendingRequests = findViewById(R.id.tv_pending_requests);
        cardManageInventory = findViewById(R.id.card_manage_inventory);
        cardHandleRequests = findViewById(R.id.card_handle_requests);
        cardViewDonors = findViewById(R.id.card_view_donors);
        cardLogout = findViewById(R.id.card_logout);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Staff Dashboard");
        }
    }

    private void setupWelcomeMessage() {
        String staffName = sessionManager.getUserName();
        if (staffName != null && !staffName.isEmpty()) {
            tvStaffWelcome.setText("Welcome, " + staffName);
        } else {
            tvStaffWelcome.setText("Welcome, Staff Member!");
        }
    }

    private void setupClickListeners() {

        cardManageInventory.setOnClickListener(v -> {

            Intent intent = new Intent(StaffDashboardActivity.this, ManageInventoryActivity.class);
            startActivity(intent);
        });


        cardHandleRequests.setOnClickListener(v -> {
            // Toast.makeText(this, "Handle Requests Clicked", Toast.LENGTH_SHORT).show(); // Dòng cũ
            Intent intent = new Intent(StaffDashboardActivity.this, ViewRequestsActivity.class);
            startActivity(intent);
        });

        cardViewDonors.setOnClickListener(v -> {
            Intent intent = new Intent(StaffDashboardActivity.this, DonorListActivity.class);
            startActivity(intent);
        });

        cardLogout.setOnClickListener(v -> showLogoutConfirmationDialog());
    }

    private void showLogoutConfirmationDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Confirm Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Logout", (dialog, which) -> logout())
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void loadDashboardData() {
        // Load total blood units
        Map<String, Integer> bloodCounts = dbHelper.getBloodGroupCounts();
        int totalUnits = 0;
        for (int units : bloodCounts.values()) {
            totalUnits += units;
        }
        tvTotalUnits.setText(String.valueOf(totalUnits));

        // Load pending requests
        int pendingRequests = dbHelper.countPendingRequests();
        tvPendingRequests.setText(String.valueOf(pendingRequests));
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        sessionManager.logoutUser();
    }
}
