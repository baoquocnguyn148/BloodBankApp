package com.example.bloodbankapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

// Import các lớp cần thiết
import com.example.bloodbankapp.R;
import com.example.bloodbankapp.database.DatabaseHelper;
import com.example.bloodbankapp.models.User;
import com.example.bloodbankapp.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

public class DonorDashboard extends AppCompatActivity {

    private MaterialButton btnEditProfile, btnScheduleDonation, btnViewRequests, btnNotifications, btnLogout;
    private TextView tvDonorName, tvDonorEmail, tvBloodGroup, tvPhone, tvAddress;

    private SessionManager sessionManager;
    private DatabaseHelper dbHelper;
    private ActivityResultLauncher<Intent> editProfileLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_dashboard);

        // Khởi tạo SessionManager và DB Helper
        sessionManager = new SessionManager(this);
        dbHelper = new DatabaseHelper(this);

        // Kiểm tra nếu chưa đăng nhập thì quay về Login
        if (!sessionManager.isLoggedIn()) {
            goToLoginActivity();
            return;
        }

        // Đăng ký launcher để nhận kết quả và làm mới dữ liệu khi quay về
        editProfileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Nếu màn hình Edit trả về kết quả OK, tải lại dữ liệu
                        loadUserData();
                        Toast.makeText(this, "Profile reloaded!", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Donor Dashboard");
        }

        initViews();
        setupClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Luôn tải lại dữ liệu khi màn hình được hiển thị
        loadUserData();
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
    }

    private void setupClickListeners() {
        // Nút Edit Profile
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(DonorDashboard.this, EditProfileActivity.class);
            int currentUserId = sessionManager.getUserId();
            intent.putExtra("USER_ID_TO_EDIT", currentUserId);
            editProfileLauncher.launch(intent);
        });

        // Nút View Requests: Mở màn hình ViewRequestsActivity
        btnViewRequests.setOnClickListener(v -> {
            Intent intent = new Intent(DonorDashboard.this, ViewRequestsActivity.class);
            startActivity(intent);
        });

        btnScheduleDonation.setOnClickListener(v -> {
            // Xóa dòng Toast cũ
            // Toast.makeText(this, "Schedule Donation feature is coming soon!", Toast.LENGTH_SHORT).show();

            // Thêm code để mở màn hình ScheduleDonationActivity
            Intent intent = new Intent(DonorDashboard.this, ScheduleDonationActivity.class);
            startActivity(intent);
        });

        btnNotifications.setOnClickListener(v -> {
            // Tạm thời vẫn giữ "Coming Soon" cho chức năng này
            Toast.makeText(this, "Notifications feature is coming soon!", Toast.LENGTH_SHORT).show();
        });


        // Nút Logout
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> showLogoutConfirmationDialog());
        }
    }

    private void loadUserData() {
        int userId = sessionManager.getUserId();
        if (userId != -1) {
            User currentUser = dbHelper.getUserById(userId);
            if (currentUser != null) {
                tvDonorName.setText(currentUser.getName());
                tvDonorEmail.setText(currentUser.getEmail());
                tvPhone.setText(currentUser.getPhone());
                tvAddress.setText(currentUser.getAddress());
                tvBloodGroup.setText(currentUser.getBloodGroup());
            } else {
                Toast.makeText(this, "Failed to load user details from DB.", Toast.LENGTH_SHORT).show();
                performLogout();
            }
        } else {
            performLogout();
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
        sessionManager.logoutUser();
        // Không cần gọi goToLoginActivity() nữa vì logoutUser() đã tự chuyển màn hình
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(DonorDashboard.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
