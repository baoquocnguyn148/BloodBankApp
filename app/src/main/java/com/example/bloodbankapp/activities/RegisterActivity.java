package com.example.bloodbankapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log; // ✅ Import Log
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bloodbankapp.R;
// import com.example.bloodbankapp.database.DatabaseHelper; // ❌ Không dùng DatabaseHelper
import com.example.bloodbankapp.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore; // ✅ Import Firestore

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPhone, etAddress, etPassword, etConfirmPassword;
    private AutoCompleteTextView spBloodGroup, spUserType;
    private MaterialButton btnRegister;
    private ProgressBar progressBar;

    // private DatabaseHelper dbHelper; // ❌ Không dùng DatabaseHelper
    private FirebaseAuth mAuth;
    private FirebaseFirestore dbFirestore; // ✅ Khai báo Firestore instance

    private static final String TAG = "RegisterActivity"; // Thêm TAG

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupAdapters();

        mAuth = FirebaseAuth.getInstance();
        dbFirestore = FirebaseFirestore.getInstance(); // ✅ Khởi tạo Firestore
        // dbHelper = new DatabaseHelper(this); // ❌ Xóa dòng này

        btnRegister.setOnClickListener(v -> registerUserWithFirebase());

        findViewById(R.id.tv_login).setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void initViews() {
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etAddress = findViewById(R.id.et_address);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        spBloodGroup = findViewById(R.id.sp_blood_group);
        spUserType = findViewById(R.id.sp_user_type);
        btnRegister = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupAdapters() {
        ArrayAdapter<CharSequence> bloodAdapter = ArrayAdapter.createFromResource(this, R.array.blood_groups, android.R.layout.simple_dropdown_item_1line);
        spBloodGroup.setAdapter(bloodAdapter);

        ArrayAdapter<CharSequence> roleAdapter = ArrayAdapter.createFromResource(this, R.array.user_roles, android.R.layout.simple_dropdown_item_1line);
        spUserType.setAdapter(roleAdapter);
    }

    private void registerUserWithFirebase() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String bloodGroup = spBloodGroup.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        // Lấy role và chuyển thành chữ thường, thay thế khoảng trắng/ký tự đặc biệt nếu cần
        String role = spUserType.getText().toString().trim().toLowerCase().replace(" ", "_");


        // --- Phần kiểm tra dữ liệu nhập (giữ nguyên) ---
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) ||
                TextUtils.isEmpty(address) || TextUtils.isEmpty(bloodGroup) || TextUtils.isEmpty(role) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            return;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            return;
        }
        // --- Kết thúc kiểm tra ---


        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        // 1. Tạo tài khoản trên Firebase Auth
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, authTask -> {
                    if (authTask.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String uid = firebaseUser.getUid();

                            // ✅ 2. Tạo đối tượng User với constructor đúng (String uid, ...)
                            // Đảm bảo bạn đã thêm constructor này vào User.java
                            User newUser = new User(uid, email, name, phone, address, bloodGroup, role);

                            // ✅ 3. Lưu đối tượng User vào Firestore
                            dbFirestore.collection("users").document(uid) // Sử dụng uid làm ID document
                                    .set(newUser) // Lưu toàn bộ đối tượng User
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "User profile created in Firestore for UID: " + uid);
                                        progressBar.setVisibility(View.GONE);
                                        btnRegister.setEnabled(true);
                                        Toast.makeText(RegisterActivity.this, "Registration successful! Please login.", Toast.LENGTH_LONG).show();
                                        mAuth.signOut(); // Đăng xuất để yêu cầu đăng nhập lại
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        // ⚠️ Lỗi khi lưu vào Firestore
                                        Log.w(TAG, "Error adding user document to Firestore", e);
                                        progressBar.setVisibility(View.GONE);
                                        btnRegister.setEnabled(true);
                                        Toast.makeText(RegisterActivity.this, "Registration failed: Could not save user details. Please try again.", Toast.LENGTH_LONG).show();
                                        // Xóa tài khoản Auth vừa tạo để tránh user bị treo
                                        firebaseUser.delete().addOnCompleteListener(deleteTask -> {
                                            if (deleteTask.isSuccessful()) {
                                                Log.d(TAG, "Firebase Auth user deleted after Firestore failure.");
                                            } else {
                                                Log.w(TAG, "Failed to delete Firebase Auth user after Firestore failure.", deleteTask.getException());
                                            }
                                        });
                                    });

                        } else {
                            // Trường hợp hiếm gặp: Auth thành công nhưng firebaseUser là null
                            progressBar.setVisibility(View.GONE);
                            btnRegister.setEnabled(true);
                            Log.w(TAG, "createUserWithEmail:success, but FirebaseUser is null");
                            Toast.makeText(RegisterActivity.this, "Registration failed: Could not get user information.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // Đăng ký Auth thất bại
                        progressBar.setVisibility(View.GONE);
                        btnRegister.setEnabled(true);
                        Log.w(TAG, "createUserWithEmail:failure", authTask.getException());
                        Toast.makeText(RegisterActivity.this, "Registration Failed: " + authTask.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}