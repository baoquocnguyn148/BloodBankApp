package com.example.bloodbankapp.activities;

import android.os.Bundle;
import android.text.TextUtils; // Import TextUtils
import android.util.Log;      // Import Log
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.bloodbankapp.R;
// import com.example.bloodbankapp.database.DatabaseHelper; // ❌ Không dùng SQLite nữa
import com.example.bloodbankapp.models.Request; // ✅ Sử dụng model Request
import com.example.bloodbankapp.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth; // ✅ Import FirebaseAuth
import com.google.firebase.auth.FirebaseUser; // ✅ Import FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore; // ✅ Import Firestore
import com.google.firebase.firestore.FieldValue; // ✅ Import FieldValue for timestamp

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap; // ✅ Import HashMap
import java.util.Locale;
import java.util.Map;   // ✅ Import Map

public class AddBloodRequestActivity extends AppCompatActivity {

    private TextInputEditText etPatientName, etHospital, etQuantity;
    private AutoCompleteTextView actvBloodGroup;
    private Button btnSubmit;
    private Toolbar toolbar;

    // private DatabaseHelper dbHelper; // ❌ Không dùng SQLite nữa
    private FirebaseFirestore dbFirestore; // ✅ Khai báo Firestore
    private SessionManager sessionManager;
    private FirebaseAuth mAuth; // ✅ Khai báo FirebaseAuth

    private static final String TAG = "AddBloodRequest"; // Thêm TAG

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_blood_request);

        initViews();
        setupToolbar();
        setupBloodGroupSpinner();

        // Khởi tạo helper và Firebase
        // dbHelper = new DatabaseHelper(this); // ❌
        dbFirestore = FirebaseFirestore.getInstance(); // ✅ Khởi tạo Firestore
        sessionManager = new SessionManager(this);
        mAuth = FirebaseAuth.getInstance(); // ✅ Khởi tạo FirebaseAuth

        btnSubmit.setOnClickListener(v -> submitRequestToFirestore()); // Đổi tên hàm
    }

    private void initViews() {
        // ... (giữ nguyên)
        toolbar = findViewById(R.id.toolbar);
        etPatientName = findViewById(R.id.et_patient_name);
        etHospital = findViewById(R.id.et_hospital);
        actvBloodGroup = findViewById(R.id.actv_blood_group);
        etQuantity = findViewById(R.id.et_quantity);
        btnSubmit = findViewById(R.id.btn_submit_request);
    }

    private void setupToolbar() {
        // ... (giữ nguyên)
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Request New Blood"); // Đảm bảo tiêu đề đúng
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupBloodGroupSpinner() {
        // ... (giữ nguyên)
        String[] bloodGroups = getResources().getStringArray(R.array.blood_groups);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, bloodGroups);
        actvBloodGroup.setAdapter(adapter);
    }

    private void submitRequestToFirestore() {
        String patientName = etPatientName.getText().toString().trim();
        String hospital = etHospital.getText().toString().trim();
        String bloodGroup = actvBloodGroup.getText().toString().trim();
        String quantityStr = etQuantity.getText().toString().trim();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Kiểm tra đăng nhập Firebase
        if (currentUser == null) {
            Toast.makeText(this, "You must be logged in to make a request.", Toast.LENGTH_SHORT).show();
            // Optional: Redirect to LoginActivity
            return;
        }
        String requesterUid = currentUser.getUid(); // Lấy UID từ Firebase Auth

        // Kiểm tra dữ liệu nhập
        if (patientName.isEmpty() || hospital.isEmpty() || bloodGroup.isEmpty() || quantityStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int units;
        try {
            units = Integer.parseInt(quantityStr);
            if (units <= 0) {
                etQuantity.setError("Quantity must be positive");
                return;
            }
        } catch (NumberFormatException e) {
            etQuantity.setError("Invalid number");
            return;
        }

        // Tạo đối tượng Map hoặc Model để lưu vào Firestore
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("requesterUid", requesterUid); // ✅ Lưu UID của người yêu cầu
        // requestData.put("recipientId", sessionManager.getUserId()); // Có thể bỏ nếu dùng requesterUid
        requestData.put("patientName", patientName);
        requestData.put("hospital", hospital);
        requestData.put("bloodGroup", bloodGroup); // Lưu tên trường là bloodGroup
        requestData.put("units", units);
        requestData.put("status", "Pending"); // Trạng thái mặc định
        requestData.put("requestTimestamp", FieldValue.serverTimestamp()); // ✅ Dùng timestamp của server

        // Lưu vào Firestore
        btnSubmit.setEnabled(false); // Vô hiệu hóa nút trong khi lưu
        dbFirestore.collection("requests") // Tên collection là "requests"
                .add(requestData) // Firestore tự tạo ID cho document
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Request submitted successfully with ID: " + documentReference.getId());
                    Toast.makeText(AddBloodRequestActivity.this, "Request submitted successfully!", Toast.LENGTH_LONG).show();
                    finish(); // Đóng màn hình và quay lại
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding request document", e);
                    Toast.makeText(AddBloodRequestActivity.this, "Failed to submit request. Please try again.", Toast.LENGTH_SHORT).show();
                    btnSubmit.setEnabled(true); // Bật lại nút nếu lỗi
                });

        /* // ❌ Xóa phần lưu vào SQLite
        int userId = sessionManager.getUserId();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        boolean success = dbHelper.addBloodRequest(userId, patientName, hospital, bloodGroup, units, "Pending", currentDate);
        if (success) { ... } else { ... }
        */
    }
}