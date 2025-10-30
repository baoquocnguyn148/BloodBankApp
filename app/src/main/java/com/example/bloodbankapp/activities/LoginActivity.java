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

import androidx.annotation.NonNull; // Thêm import này
import androidx.appcompat.app.AppCompatActivity;

import com.example.bloodbankapp.R;
import com.example.bloodbankapp.database.DatabaseHelper; // ✅ Cần dùng để sync SQLite
import com.example.bloodbankapp.models.User;
import com.example.bloodbankapp.utils.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener; // Thêm import
import com.google.android.gms.tasks.Task; // Thêm import
import com.google.firebase.auth.AuthResult; // Thêm import
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference; // ✅ Import Firestore
import com.google.firebase.firestore.DocumentSnapshot; // ✅ Import Firestore
import com.google.firebase.firestore.FirebaseFirestore; // ✅ Import Firestore

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private DatabaseHelper dbHelper; // ✅ Dùng để sync SQLite
    private FirebaseFirestore dbFirestore; // ✅ Khai báo Firestore instance
    private SessionManager sessionManager;

    private static final String TAG = "LoginActivity"; // Thêm TAG

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        dbFirestore = FirebaseFirestore.getInstance(); // ✅ Khởi tạo Firestore
        dbHelper = new DatabaseHelper(this); // ✅ Khởi tạo để sync SQLite
        sessionManager = new SessionManager(this);

        initViews();
        setupClickListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Kiểm tra session hiện có (logic này vẫn giữ nguyên)
        if (sessionManager.isLoggedIn()) {
            String role = sessionManager.getUserRole();
            if (role != null && !role.trim().isEmpty()) {
                navigateToDashboard(role);
            }
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

        // 🔥 HARDCODED ADMIN LOGIN - BYPASS FIREBASE
        if (email.equals("admin@bloodbank.com") && password.equals("admin123")) {
            Log.d(TAG, "Hardcoded admin login detected - bypassing Firebase Auth");
            
            progressBar.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);
            
            // Tạo admin user object
            User adminUser = new User();
            adminUser.setUserId(1); // ID cố định cho admin
            adminUser.setEmail("admin@bloodbank.com");
            adminUser.setName("System Admin");
            adminUser.setPhone("0000000000");
            adminUser.setAddress("Blood Bank HQ");
            adminUser.setBloodGroup("N/A");
            adminUser.setRole("admin");
            
            // Tạo session và login
            sessionManager.createLoginSession(
                    1,
                    "System Admin",
                    "admin@bloodbank.com",
                    "0000000000",
                    "Blood Bank HQ",
                    "N/A",
                    "admin"
            );
            
            progressBar.setVisibility(View.GONE);
            btnLogin.setEnabled(true);
            
            Toast.makeText(LoginActivity.this, "Welcome Admin!", Toast.LENGTH_SHORT).show();
            navigateToDashboard("admin");
            return;
        }

        // ✅ Thêm Log để kiểm tra giá trị email/password gửi đi
        Log.d(TAG, "Attempting login with Email: [" + email + "], Password provided (length): " + password.length());

        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        // 1. Đăng nhập bằng Firebase Auth
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, authTask -> {
                    // Luôn ẩn progress bar và bật lại nút
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);

                    if (authTask.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser firebaseUser = authTask.getResult().getUser();
                        if (firebaseUser != null) {
                            // ✅ 2. Lấy thông tin chi tiết từ Firestore
                            fetchUserDetailsFromFirestore(firebaseUser);
                        } else {
                            Log.w(TAG, "signInWithEmail:success, but FirebaseUser is null");
                            Toast.makeText(LoginActivity.this, "Login failed: Could not get user information.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // Đăng nhập Auth thất bại
                        Log.w(TAG, "signInWithEmail:failure", authTask.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed. Check your credentials.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    // ✅ Hàm mới để lấy thông tin từ Firestore
    private void fetchUserDetailsFromFirestore(FirebaseUser firebaseUser) {
        String uid = firebaseUser.getUid();
        DocumentReference userRef = dbFirestore.collection("users").document(uid);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    Log.d(TAG, "User document found in Firestore for UID: " + uid);
                    // Chuyển đổi DocumentSnapshot thành đối tượng User
                    User user = document.toObject(User.class);

                    // Quan trọng: Kiểm tra xem user và role có hợp lệ không
                    if (user != null && user.getRole() != null && !user.getRole().trim().isEmpty()) {
                        // ✅ 3. Tạo session và điều hướng
                        handleSuccessfulFirestoreLogin(user); // Đổi tên hàm cho rõ ràng
                    } else {
                        Log.w(TAG, "Firestore document exists, but User object is null or role is missing/empty.");
                        Toast.makeText(LoginActivity.this, "Login failed: User data is incomplete.", Toast.LENGTH_LONG).show();
                        mAuth.signOut(); // Đăng xuất để thử lại
                    }
                } else {
                    // Không tìm thấy document trong Firestore (có thể user đăng ký lỗi hoặc bị xóa)
                    Log.w(TAG, "No user document found in Firestore for UID: " + uid);
                    Toast.makeText(LoginActivity.this, "Login failed: User details not found in database.", Toast.LENGTH_LONG).show();
                    mAuth.signOut(); // Đăng xuất để thử lại
                }
            } else {
                // Lỗi khi đọc Firestore
                Log.w(TAG, "Error getting user document from Firestore: ", task.getException());
                Toast.makeText(LoginActivity.this, "Login failed: Could not retrieve user details.", Toast.LENGTH_LONG).show();
                mAuth.signOut(); // Đăng xuất để thử lại
            }
        });
    }


    // ✅ Sửa lại hàm này để nhận đối tượng User từ Firestore và sync vào SQLite
    private void handleSuccessfulFirestoreLogin(User user) {
        // 🔄 SYNC USER VÀO SQLITE DATABASE
        User existingUser = dbHelper.getUserByEmail(user.getEmail());
        if (existingUser == null) {
            // Chưa có trong SQLite -> Thêm mới
            long userId = dbHelper.addUser(user);
            user.setUserId((int) userId);
            Log.d(TAG, "User synced to SQLite with ID: " + userId);
        } else {
            // Đã có rồi -> Cập nhật thông tin nếu cần
            user.setUserId(existingUser.getUserId());
            Log.d(TAG, "User already exists in SQLite with ID: " + existingUser.getUserId());
        }

        // User ID có thể dùng từ Firestore document ID (uid) hoặc từ trường userId nếu bạn vẫn dùng
        // Ở đây ưu tiên dùng uid làm định danh chính, userId (int) có thể không cần thiết
        int localUserId = user.getUserId(); // Lấy userId (int) nếu model User còn dùng
        if (localUserId <= 0) {
            // Nếu userId không hợp lệ (ví dụ = 0), gán tạm -1 hoặc bỏ qua nếu SessionManager không yêu cầu int ID
            localUserId = -1;
            Log.w(TAG,"User ID from Firestore object is invalid or missing, using -1 for session.");
        }

        // Tạo session với thông tin từ đối tượng User lấy từ Firestore
        sessionManager.createLoginSession(
                localUserId, // Truyền ID int (có thể là -1)
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getAddress(),
                user.getBloodGroup(),
                user.getRole()
        );

        Log.d(TAG, "Session created successfully for role: " + user.getRole());
        Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

        navigateToDashboard(user.getRole());
    }

    /* // ❌ Xóa hàm xử lý login cũ dùng SQLite
    private void handleSuccessfulLogin(FirebaseUser firebaseUser) { ... }
    */

    private void navigateToDashboard(String role) {
        // ... (Logic điều hướng giữ nguyên)
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
                Log.e(TAG, "Unknown role navigation attempt: '" + role + "'. Session cleared.");
                Toast.makeText(this, "Cannot navigate: Unknown user role.", Toast.LENGTH_SHORT).show();
                sessionManager.logoutUser();
                return;
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}