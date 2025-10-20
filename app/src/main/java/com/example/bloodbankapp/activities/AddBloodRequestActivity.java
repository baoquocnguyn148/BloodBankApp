package com.example.bloodbankapp.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.bloodbankapp.R;
import com.example.bloodbankapp.database.DatabaseHelper;
import com.example.bloodbankapp.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddBloodRequestActivity extends AppCompatActivity {

    // Sửa lại cho đúng với file XML
    private TextInputEditText etPatientName, etHospital, etQuantity;
    private AutoCompleteTextView actvBloodGroup;
    private Button btnSubmit;
    private Toolbar toolbar;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_blood_request);

        initViews();
        setupToolbar();
        setupBloodGroupSpinner();

        // Khởi tạo helper
        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        btnSubmit.setOnClickListener(v -> submitRequest());
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        etPatientName = findViewById(R.id.et_patient_name);
        etHospital = findViewById(R.id.et_hospital);
        // ✅ SỬA LỖI 1: Sửa lại kiểu và ID cho đúng
        actvBloodGroup = findViewById(R.id.actv_blood_group);
        etQuantity = findViewById(R.id.et_quantity); // ID đúng là et_quantity
        btnSubmit = findViewById(R.id.btn_submit_request);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            // Dòng này cho phép hiển thị nút back
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        // Bắt sự kiện khi nút back được bấm
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupBloodGroupSpinner() {
        String[] bloodGroups = getResources().getStringArray(R.array.blood_groups);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, bloodGroups);
        actvBloodGroup.setAdapter(adapter);
    }

    private void submitRequest() {
        String patientName = etPatientName.getText().toString().trim();
        String hospital = etHospital.getText().toString().trim();
        String bloodGroup = actvBloodGroup.getText().toString().trim();
        // ✅ SỬA LỖI 2: Lấy text từ etQuantity
        String quantityStr = etQuantity.getText().toString().trim();

        if (patientName.isEmpty() || hospital.isEmpty() || bloodGroup.isEmpty() || quantityStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int units = Integer.parseInt(quantityStr);
        int userId = sessionManager.getUserId();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        boolean success = dbHelper.addBloodRequest(userId, patientName, hospital, bloodGroup, units, "Pending", currentDate);

        if (success) {
            Toast.makeText(this, "Request submitted successfully!", Toast.LENGTH_LONG).show();
            finish(); // Đóng màn hình và quay lại
        } else {
            Toast.makeText(this, "Failed to submit request. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
