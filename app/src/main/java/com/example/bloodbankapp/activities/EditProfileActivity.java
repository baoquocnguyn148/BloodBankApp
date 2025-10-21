package com.example.bloodbankapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.bloodbankapp.R;
import com.example.bloodbankapp.database.DatabaseHelper;
import com.example.bloodbankapp.models.User;
import com.example.bloodbankapp.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPhone, etAddress;
    private AutoCompleteTextView etBloodGroup;
    private Button btnUpdateProfile;
    private Toolbar toolbar;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private User currentUserToEdit;
    private int userIdToEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initViews();
        setupToolbar();
        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        userIdToEdit = getIntent().getIntExtra("USER_ID_TO_EDIT", -1);

        if (userIdToEdit == -1) {
            Toast.makeText(this, "Could not load data: User ID is missing.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        setupBloodGroupAdapter();
        loadUserData();

        btnUpdateProfile.setOnClickListener(v -> updateProfile());
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_edit_profile);
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etAddress = findViewById(R.id.et_address);
        etBloodGroup = findViewById(R.id.et_blood_group);
        btnUpdateProfile = findViewById(R.id.btn_update_profile);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit Profile"); // Đặt tiêu đề cho màn hình
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void loadUserData() {
        currentUserToEdit = dbHelper.getUserById(userIdToEdit);
        if (currentUserToEdit != null) {
            etName.setText(currentUserToEdit.getName());
            etEmail.setText(currentUserToEdit.getEmail());
            etPhone.setText(currentUserToEdit.getPhone());
            etAddress.setText(currentUserToEdit.getAddress());
            etBloodGroup.setText(currentUserToEdit.getBloodGroup(), false);
        } else {
            Toast.makeText(this, "Could not load user data from database.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setupBloodGroupAdapter() {
        try {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.blood_groups, android.R.layout.simple_dropdown_item_1line);
            etBloodGroup.setAdapter(adapter);
        } catch (Exception e) {
            Toast.makeText(this, "Please define 'blood_groups' array in strings.xml", Toast.LENGTH_SHORT).show();
            String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, bloodGroups);
            etBloodGroup.setAdapter(adapter);
        }
    }

    private void updateProfile() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String bloodGroup = etBloodGroup.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(address) || TextUtils.isEmpty(bloodGroup)) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Truyền các tham số riêng lẻ vào hàm updateUser
        int rowsAffected = dbHelper.updateUser(
                userIdToEdit,
                name,
                phone,
                address,
                bloodGroup,
                currentUserToEdit.getRole()
        );

        if (rowsAffected > 0) {
            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();

            if (userIdToEdit == sessionManager.getUserId()) {

                sessionManager.createLoginSession(
                        sessionManager.getUserId(),
                        name,                         // Tên mới
                        currentUserToEdit.getEmail(), // Email không đổi
                        phone,                        // SĐT mới
                        address,                      // Địa chỉ mới
                        bloodGroup,                   // Nhóm máu mới
                        currentUserToEdit.getRole()   // Vai trò không đổi
                );
            }

            setResult(RESULT_OK, new Intent());
            finish();

        } else {
            Toast.makeText(this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
