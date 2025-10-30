package com.example.bloodbankapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log; // Import Log
import android.view.MenuItem;
import android.view.View; // Import View
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar; // Import ProgressBar (tùy chọn)
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.bloodbankapp.R;
// import com.example.bloodbankapp.database.DatabaseHelper; // ❌ Không dùng SQLite nữa
import com.example.bloodbankapp.models.User;
import com.example.bloodbankapp.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout; // Import TextInputLayout
import com.google.firebase.auth.FirebaseAuth;   // ✅ Import FirebaseAuth
import com.google.firebase.auth.FirebaseUser;   // ✅ Import FirebaseUser
import com.google.firebase.firestore.DocumentReference; // ✅ Import Firestore
import com.google.firebase.firestore.FirebaseFirestore; // ✅ Import Firestore

import java.util.HashMap; // ✅ Import HashMap
import java.util.Map;   // ✅ Import Map

public class EditProfileActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPhone, etAddress;
    private AutoCompleteTextView etBloodGroup;
    private AutoCompleteTextView etRole; // ✅ Thêm AutoCompleteTextView cho Role
    private TextInputLayout layoutRole; // ✅ Thêm TextInputLayout cho Role
    private Button btnUpdateProfile;
    private Toolbar toolbar;
    private ProgressBar progressBar; // Tùy chọn: ProgressBar

    // private DatabaseHelper dbHelper; // ❌ Không dùng SQLite nữa
    private FirebaseFirestore dbFirestore; // ✅ Khai báo Firestore
    private FirebaseAuth mAuth; // ✅ Khai báo FirebaseAuth
    private SessionManager sessionManager;
    private User currentUserToEdit; // Lưu trữ user hiện tại đang sửa
    // private int userIdToEdit; // ❌ Không dùng ID int nữa
    private String userUidToEdit; // ✅ Dùng UID String

    private boolean isAdminEditing = false; // Biến cờ để biết Admin có đang sửa user khác không
    private String originalRole; // Lưu vai trò gốc để kiểm tra

    private static final String TAG = "EditProfileActivity"; // Thêm TAG

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile); // Đảm bảo layout có ProgressBar, layoutRole nếu cần

        initViews();
        setupToolbar();

        // dbHelper = new DatabaseHelper(this); // ❌
        dbFirestore = FirebaseFirestore.getInstance(); // ✅ Khởi tạo Firestore
        mAuth = FirebaseAuth.getInstance(); // ✅ Khởi tạo FirebaseAuth
        sessionManager = new SessionManager(this);

        // --- Lấy ID/UID để biết sửa ai ---
        // Thử lấy UID trước (nếu DonorDashboard đã được cập nhật để gửi UID)
        userUidToEdit = getIntent().getStringExtra("USER_UID_TO_EDIT");

        if (userUidToEdit == null || userUidToEdit.isEmpty()) {
            // Nếu không có UID, thử lấy userId (int) cũ để tương thích ngược (ví dụ từ ManageUsers)
            int userIdInt = getIntent().getIntExtra("USER_ID_TO_EDIT", -1);
            if (userIdInt != -1) {
                // Nếu có userId (int), cần tìm UID tương ứng
                // TẠM THỜI: Lấy UID của người dùng đang đăng nhập nếu ID trùng khớp
                // CÁCH TỐT HƠN: Truyền UID từ ManageUsersActivity
                FirebaseUser loggedInUser = mAuth.getCurrentUser();
                if (loggedInUser != null && sessionManager.getUserId() == userIdInt) {
                    userUidToEdit = loggedInUser.getUid();
                    Log.d(TAG, "Editing current user based on matching int ID. UID: " + userUidToEdit);
                } else if ("admin".equals(sessionManager.getUserRole())) {
                    // Nếu là Admin sửa user khác qua userId (int), cần cách lấy UID từ int ID
                    // Tạm thời báo lỗi vì chưa có cách map int ID -> UID hiệu quả
                    Log.e(TAG, "Admin editing via int ID is not fully supported yet. Need UID mapping.");
                    Toast.makeText(this, "Admin editing via old ID not fully supported. Please use Manage Users with UID.", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                } else {
                    Log.e(TAG, "Could not determine UID from int ID: " + userIdInt);
                    Toast.makeText(this, "Could not load data: User identifier mismatch.", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }

            } else {
                // Không có cả UID lẫn ID int
                Log.e(TAG, "Could not load data: User identifier (UID or ID) is missing.");
                Toast.makeText(this, "Could not load data: User identifier is missing.", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        } else {
            Log.d(TAG, "Editing user with UID: " + userUidToEdit);
        }
        // --- Kết thúc lấy ID/UID ---

        // Kiểm tra xem Admin có đang sửa user khác không
        FirebaseUser loggedInUser = mAuth.getCurrentUser();
        if (loggedInUser != null && !loggedInUser.getUid().equals(userUidToEdit) && "admin".equals(sessionManager.getUserRole())) {
            isAdminEditing = true;
        }

        setupSpinners(); // Setup adapters cho dropdowns
        loadUserDataFromFirestore(); // Load dữ liệu từ Firestore

        btnUpdateProfile.setOnClickListener(v -> updateProfileInFirestore());
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_edit_profile);
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email); // Email vẫn giữ để hiển thị (không cho sửa)
        etPhone = findViewById(R.id.et_phone);
        etAddress = findViewById(R.id.et_address);
        etBloodGroup = findViewById(R.id.et_blood_group); // Đổi ID này trong layout nếu cần
        etRole = findViewById(R.id.et_role);             // ✅ Ánh xạ Role
        layoutRole = findViewById(R.id.layout_role);     // ✅ Ánh xạ Layout Role
        btnUpdateProfile = findViewById(R.id.btn_update_profile);
        // progressBar = findViewById(R.id.progressBar); // ✅ Ánh xạ ProgressBar nếu có
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.edit_profile_title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    // ✅ Setup adapters cho cả BloodGroup và Role
    private void setupSpinners() {
        // Blood Group Adapter
        try {
            ArrayAdapter<CharSequence> bloodAdapter = ArrayAdapter.createFromResource(this,
                    R.array.blood_groups, android.R.layout.simple_dropdown_item_1line);
            etBloodGroup.setAdapter(bloodAdapter);
        } catch (Exception e) {
            Toast.makeText(this, "Error loading blood groups. Using defaults.", Toast.LENGTH_SHORT).show();
            String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, bloodGroups);
            etBloodGroup.setAdapter(adapter);
        }

        // Role Adapter (Chỉ hiển thị nếu Admin đang sửa)
        try {
            // Bao gồm cả vai trò "admin" nếu admin đang sửa
            ArrayAdapter<CharSequence> roleAdapter = ArrayAdapter.createFromResource(this,
                    isAdminEditing ? R.array.user_roles_admin : R.array.user_roles, // Tạo array mới user_roles_admin gồm cả admin
                    android.R.layout.simple_dropdown_item_1line);
            etRole.setAdapter(roleAdapter);
        } catch (Exception e) {
            Toast.makeText(this, "Error loading roles. Using defaults.", Toast.LENGTH_SHORT).show();
            String[] roles = {"donor", "recipient", "blood_bank_staff"}; // Mảng mặc định không có admin
            if (isAdminEditing) roles = new String[]{"donor", "recipient", "blood_bank_staff", "admin"}; // Thêm admin nếu cần
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, roles);
            etRole.setAdapter(adapter);
        }

        // Hiển thị dropdown Role chỉ khi Admin chỉnh sửa
        layoutRole.setVisibility(isAdminEditing ? View.VISIBLE : View.GONE);
    }


    // ✅ Load dữ liệu từ Firestore
    private void loadUserDataFromFirestore() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        DocumentReference userRef = dbFirestore.collection("users").document(userUidToEdit);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (progressBar != null) progressBar.setVisibility(View.GONE);
            if (documentSnapshot.exists()) {
                currentUserToEdit = documentSnapshot.toObject(User.class);
                if (currentUserToEdit != null) {
                    etName.setText(currentUserToEdit.getName());
                    etEmail.setText(currentUserToEdit.getEmail()); // Hiển thị email (không cho sửa)
                    etPhone.setText(currentUserToEdit.getPhone());
                    etAddress.setText(currentUserToEdit.getAddress());
                    etBloodGroup.setText(currentUserToEdit.getBloodGroup(), false); // false để không lọc lại dropdown
                    originalRole = currentUserToEdit.getRole(); // Lưu vai trò gốc
                    etRole.setText(originalRole, false); // Hiển thị vai trò hiện tại
                    etEmail.setEnabled(false); // Không cho sửa email
                } else {
                    Log.e(TAG, "Failed to parse user data for UID: " + userUidToEdit);
                    Toast.makeText(this, "Error loading user data.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                Log.e(TAG, "User document not found for UID: " + userUidToEdit);
                Toast.makeText(this, "User not found.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(e -> {
            if (progressBar != null) progressBar.setVisibility(View.GONE);
            Log.e(TAG, "Error fetching user data from Firestore for UID: " + userUidToEdit, e);
            Toast.makeText(this, "Error loading user profile.", Toast.LENGTH_SHORT).show();
            finish();
        });

        /* // ❌ Xóa code đọc từ SQLite
        currentUserToEdit = dbHelper.getUserById(userIdToEdit);
        if (currentUserToEdit != null) { ... } else { ... }
        */
    }


    // ✅ Cập nhật profile trên Firestore
    private void updateProfileInFirestore() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String bloodGroup = etBloodGroup.getText().toString().trim();
        String role = etRole.getText().toString().trim().toLowerCase().replace(" ", "_"); // Lấy vai trò mới

        // Kiểm tra dữ liệu nhập (trừ email và role nếu không phải admin sửa)
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(address) || TextUtils.isEmpty(bloodGroup)) {
            Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isAdminEditing && TextUtils.isEmpty(role)) {
            Toast.makeText(this, "Role cannot be empty when editing.", Toast.LENGTH_SHORT).show();
            return;
        }
        // Ngăn Admin tự xóa vai trò admin của chính mình hoặc admin khác
        FirebaseUser loggedInUser = mAuth.getCurrentUser();
        if (isAdminEditing && "admin".equalsIgnoreCase(originalRole) && !"admin".equalsIgnoreCase(role)) {
            if (loggedInUser != null && loggedInUser.getUid().equals(userUidToEdit)){
                Toast.makeText(this, "Cannot remove your own admin role.", Toast.LENGTH_SHORT).show();
                etRole.setText(originalRole, false); // Reset lại dropdown
                return;
            }
            // Cân nhắc thêm cảnh báo nếu Admin hạ quyền Admin khác
        }


        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        btnUpdateProfile.setEnabled(false);

        DocumentReference userRef = dbFirestore.collection("users").document(userUidToEdit);

        // Tạo Map chứa các trường cần cập nhật
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("phone", phone);
        updates.put("address", address);
        updates.put("bloodGroup", bloodGroup);
        if (isAdminEditing) { // Chỉ cập nhật role nếu admin đang sửa
            updates.put("role", role);
        }

        userRef.update(updates) // Chỉ update các trường trong Map
                .addOnSuccessListener(aVoid -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    btnUpdateProfile.setEnabled(true);
                    Log.d(TAG, "User profile updated successfully in Firestore for UID: " + userUidToEdit);
                    Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();

                    // Cập nhật lại Session nếu người dùng tự sửa thông tin của mình
                    if (!isAdminEditing) {
                        sessionManager.createLoginSession(
                                sessionManager.getUserId(), // Giữ ID int cũ
                                name,
                                currentUserToEdit.getEmail(), // Email không đổi
                                phone,
                                address,
                                bloodGroup,
                                currentUserToEdit.getRole() // Role không đổi khi tự sửa
                        );
                    }

                    setResult(RESULT_OK, new Intent()); // Báo cho Activity trước đó biết là đã thành công
                    finish();
                })
                .addOnFailureListener(e -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    btnUpdateProfile.setEnabled(true);
                    Log.w(TAG, "Error updating user document in Firestore for UID: " + userUidToEdit, e);
                    Toast.makeText(this, "Failed to update profile. Please try again.", Toast.LENGTH_SHORT).show();
                });


        /* // ❌ Xóa code cập nhật SQLite
        int rowsAffected = dbHelper.updateUser(
                userIdToEdit, name, phone, address, bloodGroup,
                isAdminEditing ? role : currentUserToEdit.getRole() // Chỉ cập nhật role nếu admin sửa
        );
        if (rowsAffected > 0) { ... } else { ... }
        */
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // setResult(RESULT_CANCELED); // Có thể set Cancelled nếu người dùng back mà không lưu
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}