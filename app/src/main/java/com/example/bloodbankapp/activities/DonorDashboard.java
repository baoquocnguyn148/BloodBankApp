package com.example.bloodbankapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloodbankapp.R;
import com.example.bloodbankapp.adapters.DonationHistoryAdapter;
import com.example.bloodbankapp.database.DatabaseHelper; // Vẫn dùng tạm dbHelper
import com.example.bloodbankapp.models.Donation;
import com.example.bloodbankapp.models.User;
import com.example.bloodbankapp.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser; // Import FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore; // Import Firestore
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DonorDashboard extends AppCompatActivity {

    private MaterialButton btnEditProfile, btnScheduleDonation, btnViewRequests, btnNotifications, btnLogout;
    private TextView tvDonorName, tvDonorEmail, tvBloodGroup, tvPhone, tvAddress;
    private TextView tvTotalDonations, tvLivesSaved, tvDonorLevel;

    private RecyclerView rvDonationHistory;
    private DonationHistoryAdapter donationHistoryAdapter;
    private List<Donation> donationList;

    private SessionManager sessionManager;
    private DatabaseHelper dbHelper; // Vẫn dùng tạm
    private FirebaseFirestore dbFirestore;
    private FirebaseAuth mAuth;

    private ActivityResultLauncher<Intent> editProfileLauncher;

    private static final String TAG = "DonorDashboard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_dashboard);

        sessionManager = new SessionManager(this);
        dbHelper = new DatabaseHelper(this); // Vẫn khởi tạo dbHelper
        dbFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUserAuth = mAuth.getCurrentUser();
        // Kiểm tra đăng nhập và vai trò
        if (currentUserAuth == null || !sessionManager.isLoggedIn() || !"donor".equals(sessionManager.getUserRole())) {
            performLogout(); // Logout nếu không hợp lệ
            return; // Dừng thực thi onCreate
        }

        // Đăng ký ActivityResultLauncher
        editProfileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        loadUserDataFromFirestore(); // Load lại từ Firestore sau khi sửa
                        Toast.makeText(this, "Profile reloaded!", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Donor Dashboard");
        }

        // Ánh xạ Views và Setup
        initViews();
        setupRecyclerView();
        setupClickListeners();

        // Load data lần đầu trong onResume
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Load lại dữ liệu mỗi khi quay lại màn hình
        loadUserDataFromFirestore();
        loadDonationHistoryData();
    }

    private void initViews() {
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnScheduleDonation = findViewById(R.id.btn_schedule_donation);
        btnViewRequests = findViewById(R.id.btn_view_requests);
        btnNotifications = findViewById(R.id.btn_notifications);
        btnLogout = findViewById(R.id.btn_logout);

        tvDonorName = findViewById(R.id.tv_donor_name);
        tvDonorEmail = findViewById(R.id.tv_donor_email);
        tvBloodGroup = findViewById(R.id.tv_donor_blood_group);
        tvPhone = findViewById(R.id.tv_donor_phone);
        tvAddress = findViewById(R.id.tv_donor_address);

        // Ánh xạ các TextView thống kê
        tvTotalDonations = findViewById(R.id.tv_total_donations);
        tvLivesSaved = findViewById(R.id.tv_lives_saved);
        tvDonorLevel = findViewById(R.id.tv_donor_level);

        // Ánh xạ RecyclerView
        rvDonationHistory = findViewById(R.id.rv_donation_history);
    }

    // Hàm setup RecyclerView
    private void setupRecyclerView() {
        donationList = new ArrayList<>(); // Khởi tạo list
        donationHistoryAdapter = new DonationHistoryAdapter(donationList); // Khởi tạo adapter
        rvDonationHistory.setLayoutManager(new LinearLayoutManager(this));
        rvDonationHistory.setAdapter(donationHistoryAdapter); // Gán adapter
        rvDonationHistory.setNestedScrollingEnabled(false); // Quan trọng nếu dùng trong NestedScrollView
    }

    private void setupClickListeners() {
        btnEditProfile.setOnClickListener(v -> {
            FirebaseUser user = mAuth.getCurrentUser(); // Lấy user hiện tại từ Firebase Auth
            if (user != null) {
                Intent intent = new Intent(DonorDashboard.this, EditProfileActivity.class);

                // ✅ Gửi UID (String) thay vì userId (int)
                String currentUserUid = user.getUid();
                Log.d(TAG, "Sending USER_UID_TO_EDIT: " + currentUserUid); // Thêm Log để kiểm tra
                intent.putExtra("USER_UID_TO_EDIT", currentUserUid); // Gửi UID

                editProfileLauncher.launch(intent); // Mở màn hình Edit Profile
            } else {
                // Xử lý trường hợp không lấy được user từ Firebase Auth
                Toast.makeText(this, "Cannot edit profile. User authentication error.", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "EditProfile clicked but mAuth.getCurrentUser() is null.");
            }
        });

        // --- Các listener khác giữ nguyên ---
        btnViewRequests.setOnClickListener(v -> {
            Intent intent = new Intent(DonorDashboard.this, ViewRequestsActivity.class);
            startActivity(intent);
        });
        btnScheduleDonation.setOnClickListener(v -> {
            Intent intent = new Intent(DonorDashboard.this, ScheduleDonationActivity.class);
            startActivity(intent);
        });
        btnNotifications.setOnClickListener(v -> {
            // Chuyển sang NotificationsActivity thay vì Toast
            Intent intent = new Intent(DonorDashboard.this, NotificationsActivity.class);
            startActivity(intent);
            // Toast.makeText(this, "Notifications feature is coming soon!", Toast.LENGTH_SHORT).show();
        });
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> showLogoutConfirmationDialog());
        }
        // --- Kết thúc các listener khác ---
    }

    // Load user data từ Firestore
    private void loadUserDataFromFirestore() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            performLogout();
            return;
        }
        String userUid = firebaseUser.getUid();

        dbFirestore.collection("users").document(userUid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User currentUser = documentSnapshot.toObject(User.class);
                        if (currentUser != null) {
                            // Cập nhật UI
                            tvDonorName.setText(currentUser.getName() != null ? currentUser.getName() : "N/A");
                            tvDonorEmail.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "N/A");
                            tvPhone.setText(currentUser.getPhone() != null ? currentUser.getPhone() : "N/A");
                            tvAddress.setText(currentUser.getAddress() != null ? currentUser.getAddress() : "N/A");
                            tvBloodGroup.setText(currentUser.getBloodGroup() != null ? currentUser.getBloodGroup() : "N/A");

                            // Cập nhật lại session
                            sessionManager.createLoginSession(
                                    sessionManager.getUserId(), // Giữ ID int cũ (-1 nếu chưa có)
                                    currentUser.getName(), currentUser.getEmail(), currentUser.getPhone(),
                                    currentUser.getAddress(), currentUser.getBloodGroup(), currentUser.getRole()
                            );
                        } else {
                            Log.w(TAG, "User document exists but could not be parsed for UID: " + userUid);
                            Toast.makeText(this, "Failed to load user details.", Toast.LENGTH_SHORT).show();
                            performLogout();
                        }
                    } else {
                        Log.w(TAG, "User document does not exist in Firestore for UID: " + userUid);
                        Toast.makeText(this, "User details not found.", Toast.LENGTH_SHORT).show();
                        performLogout();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching user data from Firestore for UID: " + userUid, e);
                    Toast.makeText(this, "Error loading profile. Using cached data.", Toast.LENGTH_SHORT).show();
                    // Load từ session nếu lỗi
                    loadUserDataFromSessionFallback();
                });
    }

    // Hàm dự phòng đọc từ session nếu Firestore lỗi
    private void loadUserDataFromSessionFallback() {
        Log.d(TAG, "Loading user data from session as fallback.");
        tvDonorName.setText(sessionManager.getUserName() != null ? sessionManager.getUserName() : "N/A");
        tvDonorEmail.setText(sessionManager.getUserDetails().get(SessionManager.KEY_EMAIL)); // Ví dụ lấy email
        tvPhone.setText(sessionManager.getUserDetails().get(SessionManager.KEY_PHONE));
        tvAddress.setText(sessionManager.getUserDetails().get(SessionManager.KEY_ADDRESS));
        tvBloodGroup.setText(sessionManager.getUserDetails().get(SessionManager.KEY_BLOOD_GROUP));
    }


    // Hàm load lịch sử hiến máu (hiện dùng dữ liệu giả)
    private void loadDonationHistoryData() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) return;
        String userUid = firebaseUser.getUid();

        // TODO: Implement Firestore query to get donations for userUid
        // Ví dụ: dbFirestore.collection("donations").whereEqualTo("donorUid", userUid)...

        // --- Dữ liệu giả lập ---
        donationList.clear(); // Xóa dữ liệu cũ
        donationList.add(new Donation("Central Blood Bank", 1, "2025-08-15", "Completed"));
        donationList.add(new Donation("City General Hospital", 1, "2025-05-02", "Completed"));
        // Thêm vài dòng nữa nếu muốn
        // donationList.add(new Donation("District 5 Center", 1, "2025-01-10", "Completed"));
        // --- Kết thúc dữ liệu giả lập ---

        donationHistoryAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView

        // Cập nhật các thẻ thống kê
        int totalDonations = donationList.size();
        tvTotalDonations.setText(String.valueOf(totalDonations));
        tvLivesSaved.setText(String.valueOf(totalDonations * 3)); // Giả sử 1 lần cứu 3 người

        // Xác định level donor
        if (totalDonations >= 10) {
            tvDonorLevel.setText("Gold");
        } else if (totalDonations >= 5) {
            tvDonorLevel.setText("Silver");
        } else if (totalDonations > 0) {
            tvDonorLevel.setText("Bronze");
        } else {
            tvDonorLevel.setText("New"); // Hoặc để trống
        }
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
        sessionManager.logoutUser(); // Hàm này tự động chuyển về HomeActivity -> LoginActivity
    }
}