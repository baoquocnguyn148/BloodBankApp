package com.example.bloodbankapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloodbankapp.R;
import com.example.bloodbankapp.adapters.RequestAdapter;
import com.example.bloodbankapp.database.DatabaseHelper;
import com.example.bloodbankapp.models.Request;
import com.example.bloodbankapp.models.User;
import com.example.bloodbankapp.utils.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RecipientDashboard extends AppCompatActivity {

    private TextView tvWelcome, tvPendingCount, tvApprovedCount, tvDonorsCount;
    private TextView tvNoRequestsFound, tvViewAllRequests;
    private Button btnLogout;
    private CardView cardPending, cardApproved, cardDonors;
    private SessionManager sessionManager;
    private DatabaseHelper dbHelper;

    private RecyclerView rvRecentRequests;
    private RequestAdapter recentRequestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient_dashboard);

        sessionManager = new SessionManager(this);
        dbHelper = new DatabaseHelper(this);

        if (!sessionManager.isLoggedIn() || !"recipient".equals(sessionManager.getUserRole())) {
            sessionManager.logoutUser();
            return;
        }

        initViews();
        setupToolbar();
        setupClickListeners();
        setupRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvWelcome = findViewById(R.id.tv_welcome);
        btnLogout = findViewById(R.id.btn_logout);
        cardPending = findViewById(R.id.card_pending);
        cardApproved = findViewById(R.id.card_approved);
        cardDonors = findViewById(R.id.card_donors);

        tvPendingCount = findViewById(R.id.tv_pending_count);
        tvApprovedCount = findViewById(R.id.tv_approved_count);
        tvDonorsCount = findViewById(R.id.tv_donors_count);

        rvRecentRequests = findViewById(R.id.rv_recent_requests);
        tvNoRequestsFound = findViewById(R.id.tv_no_requests_found);
        tvViewAllRequests = findViewById(R.id.tv_view_all_requests);
    }

    private void setupToolbar() {
        // Hiện tại không cần thêm gì
    }

    private void setupClickListeners() {
        btnLogout.setOnClickListener(v -> showLogoutConfirmationDialog());

        View.OnClickListener viewAllListener = v -> startActivity(new Intent(this, MyRequestsActivity.class));
        cardPending.setOnClickListener(viewAllListener);
        cardApproved.setOnClickListener(viewAllListener);
        tvViewAllRequests.setOnClickListener(viewAllListener);

        cardDonors.setOnClickListener(v -> startActivity(new Intent(this, DonorListActivity.class)));
        findViewById(R.id.btn_request_new_blood).setOnClickListener(v -> startActivity(new Intent(this, AddBloodRequestActivity.class)));
        findViewById(R.id.btn_search_donors).setOnClickListener(v -> startActivity(new Intent(this, DonorListActivity.class)));
        findViewById(R.id.btn_my_requests).setOnClickListener(v -> startActivity(new Intent(this, MyRequestsActivity.class)));
        findViewById(R.id.btn_blood_availability).setOnClickListener(v -> Toast.makeText(this, "Blood Availability clicked", Toast.LENGTH_SHORT).show());
        findViewById(R.id.btn_notifications).setOnClickListener(v -> Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show());
    }

    private void setupRecyclerView() {
        recentRequestAdapter = new RequestAdapter(this, new ArrayList<>(), false);
        rvRecentRequests.setLayoutManager(new LinearLayoutManager(this));
        rvRecentRequests.setAdapter(recentRequestAdapter);
        rvRecentRequests.setHasFixedSize(true);
    }

    private void loadDashboardData() {
        // ✅✅✅ SỬA LỖI TẠI ĐÂY ✅✅✅
        int userId = sessionManager.getUserId();
        // Giả sử hàm getUserId trả về -1 nếu không có user ID
        if (userId == -1) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            sessionManager.logoutUser();
            return;
        }

        User currentUser = dbHelper.getUserById(userId);
        if (currentUser != null) {
            // Giả sử bạn có một string resource là: <string name="welcome_recipient_placeholder">Welcome, %1$s</string>
            tvWelcome.setText(getString(R.string.welcome_recipient_placeholder, currentUser.getName()));
        }

        List<Request> allUserRequests = dbHelper.getRequestsByUserId(userId);

        long pendingCount = allUserRequests.stream().filter(r -> "Pending".equalsIgnoreCase(r.getStatus())).count();
        long approvedCount = allUserRequests.stream().filter(r -> "Approved".equalsIgnoreCase(r.getStatus())).count();
        tvPendingCount.setText(String.valueOf(pendingCount));
        tvApprovedCount.setText(String.valueOf(approvedCount));

        int donorCount = dbHelper.countUsersByRole("donor");
        tvDonorsCount.setText(donorCount + " available");


        if (allUserRequests.isEmpty()) {
            rvRecentRequests.setVisibility(View.GONE);
            tvNoRequestsFound.setVisibility(View.VISIBLE);
        } else {
            rvRecentRequests.setVisibility(View.VISIBLE);
            tvNoRequestsFound.setVisibility(View.GONE);

            List<Request> recentRequests = allUserRequests.stream().limit(3).collect(Collectors.toList());
            recentRequestAdapter.updateData(recentRequests);
        }
    }

    private void showLogoutConfirmationDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_logout_title)
                .setMessage(R.string.dialog_logout_message)
                .setPositiveButton(R.string.dialog_positive_button, (dialog, which) -> performLogout())
                .setNegativeButton(R.string.dialog_negative_button, null)
                .show();
    }

    private void performLogout() {
        FirebaseAuth.getInstance().signOut();
        sessionManager.logoutUser();
    }
}
