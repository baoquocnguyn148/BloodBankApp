package com.example.bloodbankapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bloodbankapp.R;
import com.example.bloodbankapp.database.DatabaseHelper;
import com.example.bloodbankapp.models.User;
import com.example.bloodbankapp.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupClickListeners();
    }


    @Override
    protected void onStart() {
        super.onStart();

        // Kiểm tra xem có phiên đăng nhập hợp lệ không
        if (sessionManager.isLoggedIn()) {
            String role = sessionManager.getUserRole();
            // Chỉ điều hướng nếu role hợp lệ (không null và không rỗng)
            if (role != null && !role.trim().isEmpty()) {
                navigateToDashboard(role);
            }
            // Nếu role không hợp lệ, không làm gì cả, để người dùng ở lại màn hình Login.
        }
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> loginUser());
        tvRegister.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    // Luôn ẩn progress bar và bật lại nút, dù thành công hay thất bại
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);

                    if (task.isSuccessful()) {
                        handleSuccessfulLogin(task.getResult().getUser());
                    } else {
                        Log.w("LoginActivity", "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed. Check your credentials.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void handleSuccessfulLogin(FirebaseUser firebaseUser) {
        if (firebaseUser == null) {
            Toast.makeText(this, "Login failed: Firebase user is null.", Toast.LENGTH_LONG).show();
            return;
        }

        User user = dbHelper.getUserByEmail(firebaseUser.getEmail());

        // Kiểm tra chặt chẽ hơn: user phải tồn tại VÀ có role
        if (user == null || user.getRole() == null || user.getRole().trim().isEmpty()) {
            Toast.makeText(this, "Login failed: User data is incomplete or not found.", Toast.LENGTH_LONG).show();
            mAuth.signOut(); // Đăng xuất khỏi Firebase để người dùng có thể thử lại
            return;
        }

        sessionManager.createLoginSession(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getAddress(),
                user.getBloodGroup(),
                user.getRole()
        );

        Log.d("LoginActivity", "Session created successfully for role: " + user.getRole());
        Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

        navigateToDashboard(user.getRole());
    }

    private void navigateToDashboard(String role) {
        Intent intent;
        String normalizedRole = (role != null) ? role.trim().toLowerCase() : "";

        switch (normalizedRole) {
            case "admin":
                intent = new Intent(this, AdminDashboard.class);
                break;
            case "blood_bank_staff":
                intent = new Intent(this, StaffDashboardActivity.class);
                break;
            case "donor":
                intent = new Intent(this, DonorDashboard.class);
                break;
            case "recipient":
                intent = new Intent(this, RecipientDashboard.class);
                break;
            default:
                Log.e("LoginActivity", "Unknown role navigation attempt: '" + role + "'. Session cleared.");
                Toast.makeText(this, "Cannot navigate: Unknown user role.", Toast.LENGTH_SHORT).show();
                sessionManager.logoutUser(); // Gọi hàm logout để nó tự chuyển về Login Activity một cách an toàn.
                return; // Dừng hàm lại ngay lập tức.
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Đóng LoginActivity sau khi chuyển màn hình thành công
    }
}
