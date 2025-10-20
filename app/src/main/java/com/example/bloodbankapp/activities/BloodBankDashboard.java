package com.example.bloodbankapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bloodbankapp.R;

// ✅ BƯỚC 1: XÓA CÁC IMPORT SAI, THÊM IMPORT DATABASEHELPER
import com.example.bloodbankapp.database.DatabaseHelper;
import com.example.bloodbankapp.utils.SessionManager;
import java.util.HashMap;

public class BloodBankDashboard extends AppCompatActivity {

    private Button btnLogout;
    private TextView tvTotalUnits, tvPendingRequests;
    private SessionManager sessionManager;

    // ✅ BƯỚC 2: XÓA DAO CŨ, CHỈ DÙNG DATABASEHELPER
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloodbank_dashboard);

        sessionManager = new SessionManager(this);
        // ✅ BƯỚC 3: KHỞI TẠO DATABASEHELPER
        dbHelper = new DatabaseHelper(this);

        btnLogout = findViewById(R.id.btnLogout);
        tvTotalUnits = findViewById(R.id.tvTotalUnits);
        tvPendingRequests = findViewById(R.id.tvPendingRequests);

        // loadDashboardData(); // Tạm thời gọi trong onResume để dữ liệu luôn mới

        btnLogout.setOnClickListener(v -> {
            sessionManager.logoutUser();
            startActivity(new Intent(BloodBankDashboard.this, LoginActivity.class));
            finish();
        });
    }

    // ✅ BƯỚC 5: DÙNG ONRESUME ĐỂ LUÔN TẢI DỮ LIỆU MỚI NHẤT
    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
    }

    private void loadDashboardData() {
        // ✅ BƯỚC 4: LẤY DỮ LIỆU TỪ DATABASEHELPER
        // Lưu ý: Các phương thức này cần được định nghĩa trong DatabaseHelper.java
        // File DatabaseHelper tôi gửi bạn đã có đủ các phương thức này.
        HashMap<String, Integer> bloodData = dbHelper.getBloodGroupCounts();
        int totalUnits = 0;
        for (int units : bloodData.values()) {
            totalUnits += units;
        }

        // Phương thức này cần được thêm vào DatabaseHelper
        int pendingRequests = dbHelper.countPendingRequests();

        tvTotalUnits.setText(String.valueOf(totalUnits));
        tvPendingRequests.setText(String.valueOf(pendingRequests));

        // You can display each blood group data here if your layout supports it.
    }
}
