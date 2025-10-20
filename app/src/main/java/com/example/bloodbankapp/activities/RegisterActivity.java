package com.example.bloodbankapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bloodbankapp.R;
import com.example.bloodbankapp.database.DatabaseHelper;
import com.example.bloodbankapp.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPhone, etAddress, etPassword, etConfirmPassword;
    private AutoCompleteTextView spBloodGroup, spUserType;
    private MaterialButton btnRegister;
    private ProgressBar progressBar;

    private DatabaseHelper dbHelper; // Sửa tên biến cho đúng chuẩn
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupAdapters();

        mAuth = FirebaseAuth.getInstance();
        dbHelper = new DatabaseHelper(this);

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

        // ✅✅✅ SỬA LỖI DUY NHẤT TẠI ĐÂY ✅✅✅
        // Luôn chuyển vai trò về chữ thường để đảm bảo tính nhất quán
        String role = spUserType.getText().toString().trim().toLowerCase();

        // Kiểm tra dữ liệu đầu vào
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

        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sử dụng constructor đầy đủ vì model của bạn đã có nó
                        User newUser = new User(0, email, name, phone, address, bloodGroup, role);
                        long result = dbHelper.addUser(newUser);

                        progressBar.setVisibility(View.GONE);
                        btnRegister.setEnabled(true);

                        if (result != -1) { // Kiểm tra với -1 để biết có lỗi insert hay không
                            Toast.makeText(this, "Registration successful! Please login.", Toast.LENGTH_LONG).show();
                            mAuth.signOut();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            // Nếu không lưu được vào DB, xóa user trên Firebase để tránh rác
                            Toast.makeText(this, "Failed to save user details. Rolling back.", Toast.LENGTH_SHORT).show();
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                firebaseUser.delete();
                            }
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        btnRegister.setEnabled(true);
                        Toast.makeText(RegisterActivity.this, "Registration Failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
