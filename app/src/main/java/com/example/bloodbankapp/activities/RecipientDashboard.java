package com.example.bloodbankapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloodbankapp.R;
import com.example.bloodbankapp.adapters.RequestAdapter;
import com.example.bloodbankapp.database.DatabaseHelper; // ✅ Import lại DatabaseHelper
import com.example.bloodbankapp.models.Request;
import com.example.bloodbankapp.models.User;
import com.example.bloodbankapp.utils.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RecipientDashboard extends AppCompatActivity {

    private TextView tvWelcome, tvPendingCount, tvApprovedCount, tvDonorsCount;
    private TextView tvNoRequestsFound, tvViewAllRequests;
    private Button btnLogout;
    private CardView cardPending, cardApproved, cardDonors;
    private SessionManager sessionManager;
    private FirebaseFirestore dbFirestore;
    private FirebaseAuth mAuth;

    private RecyclerView rvRecentRequests;
    private RequestAdapter recentRequestAdapter;
    private List<Request> recentRequestsList;

    private ProgressBar progressBarRequests;

    // ✅ Khai báo lại DatabaseHelper (chỉ dùng tạm để đếm donor)
    private DatabaseHelper dbHelper;

    private static final String TAG = "RecipientDashboard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient_dashboard);

        sessionManager = new SessionManager(this);
        dbFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        // ✅ Khởi tạo lại DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        FirebaseUser currentUserAuth = mAuth.getCurrentUser();
        if (currentUserAuth == null || !sessionManager.isLoggedIn() || !"recipient".equals(sessionManager.getUserRole())) {
            performLogout();
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
        loadUserData();
        loadRequestsDataFromFirestore();
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

        // progressBarRequests = findViewById(R.id.progressBarRequests);
    }

    private void setupToolbar() {
        // No additional setup needed for now
    }

    private void setupClickListeners() {
        btnLogout.setOnClickListener(v -> showLogoutConfirmationDialog());

        View.OnClickListener viewAllListener = v -> startActivity(new Intent(this, MyRequestsActivity.class));
        tvViewAllRequests.setOnClickListener(viewAllListener);
        cardPending.setOnClickListener(viewAllListener);
        cardApproved.setOnClickListener(viewAllListener);

        cardDonors.setOnClickListener(v -> startActivity(new Intent(this, DonorListActivity.class)));
        findViewById(R.id.btn_request_new_blood).setOnClickListener(v -> startActivity(new Intent(this, AddBloodRequestActivity.class)));
        findViewById(R.id.btn_search_donors).setOnClickListener(v -> startActivity(new Intent(this, DonorListActivity.class)));
        findViewById(R.id.btn_my_requests).setOnClickListener(v -> startActivity(new Intent(this, MyRequestsActivity.class)));
        findViewById(R.id.btn_blood_availability).setOnClickListener(v -> startActivity(new Intent(this, BloodAvailabilityActivity.class)));
        findViewById(R.id.btn_notifications).setOnClickListener(v -> Toast.makeText(this, "Notifications feature coming soon.", Toast.LENGTH_SHORT).show());
    }

    private void setupRecyclerView() {
        recentRequestsList = new ArrayList<>();
        recentRequestAdapter = new RequestAdapter(this, recentRequestsList, false);
        rvRecentRequests.setLayoutManager(new LinearLayoutManager(this));
        rvRecentRequests.setAdapter(recentRequestAdapter);
        rvRecentRequests.setNestedScrollingEnabled(false);
        // rvRecentRequests.setHasFixedSize(true);
    }

    private void loadUserData() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            performLogout();
            return;
        }
        String userUid = firebaseUser.getUid();
        String userName = sessionManager.getUserName();

        if (userName != null && !userName.isEmpty()) {
            tvWelcome.setText(getString(R.string.welcome_recipient_placeholder, userName));
        } else {
            dbFirestore.collection("users").document(userUid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            User user = documentSnapshot.toObject(User.class);
                            if (user != null && user.getName() != null) {
                                tvWelcome.setText(getString(R.string.welcome_recipient_placeholder, user.getName()));
                                sessionManager.createLoginSession(
                                        sessionManager.getUserId(), // Giữ ID cũ (hoặc -1)
                                        user.getName(), user.getEmail(), user.getPhone(),
                                        user.getAddress(), user.getBloodGroup(), user.getRole());
                            } else {
                                tvWelcome.setText(getString(R.string.welcome_recipient_placeholder, "Recipient"));
                            }
                        } else {
                            tvWelcome.setText(getString(R.string.welcome_recipient_placeholder, "Recipient"));
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Error getting user details from Firestore", e);
                        tvWelcome.setText(getString(R.string.welcome_recipient_placeholder, "Recipient"));
                    });
        }

        // ✅ Load số lượng Donors (vẫn từ SQLite vì dbHelper đã được khởi tạo lại)
        int donorCount = dbHelper.countUsersByRole("donor");
        tvDonorsCount.setText(donorCount + " available");
    }


    private void loadRequestsDataFromFirestore() {
        FirebaseUser currentUserAuth = mAuth.getCurrentUser();
        if (currentUserAuth == null) {
            Log.w(TAG, "Cannot load requests: User not logged in.");
            return;
        }
        String currentUserUid = currentUserAuth.getUid();

        if (progressBarRequests != null) progressBarRequests.setVisibility(View.VISIBLE);
        rvRecentRequests.setVisibility(View.GONE);
        tvNoRequestsFound.setVisibility(View.GONE);

        dbFirestore.collection("requests")
                .whereEqualTo("requesterUid", currentUserUid)
                .orderBy("requestTimestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (progressBarRequests != null) progressBarRequests.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        List<Request> allUserRequests = new ArrayList<>();
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                try {
                                    Request request = document.toObject(Request.class);
                                    request.setDocumentId(document.getId());
                                    allUserRequests.add(request);
                                } catch (Exception e){
                                    Log.e(TAG, "Error converting request document " + document.getId(), e);
                                }
                            }
                        }

                        long pendingCount = 0;
                        long approvedCount = 0;
                        for (Request req : allUserRequests) {
                            if (req.getStatus() != null) {
                                if ("Pending".equalsIgnoreCase(req.getStatus())) {
                                    pendingCount++;
                                } else if ("Approved".equalsIgnoreCase(req.getStatus())) {
                                    approvedCount++;
                                }
                            }
                        }
                        tvPendingCount.setText(String.valueOf(pendingCount));
                        tvApprovedCount.setText(String.valueOf(approvedCount));

                        recentRequestsList.clear();
                        if (!allUserRequests.isEmpty()) {
                            int limit = Math.min(allUserRequests.size(), 3);
                            recentRequestsList.addAll(allUserRequests.subList(0, limit));
                            recentRequestAdapter.notifyDataSetChanged();
                            rvRecentRequests.setVisibility(View.VISIBLE);
                            tvNoRequestsFound.setVisibility(View.GONE);
                        } else {
                            rvRecentRequests.setVisibility(View.GONE);
                            tvNoRequestsFound.setVisibility(View.VISIBLE);
                        }

                    } else {
                        Log.w(TAG, "Error getting request documents: ", task.getException());
                        Toast.makeText(RecipientDashboard.this, "Error loading requests.", Toast.LENGTH_SHORT).show();
                        tvPendingCount.setText("0");
                        tvApprovedCount.setText("0");
                        rvRecentRequests.setVisibility(View.GONE);
                        tvNoRequestsFound.setVisibility(View.VISIBLE);
                    }
                });
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