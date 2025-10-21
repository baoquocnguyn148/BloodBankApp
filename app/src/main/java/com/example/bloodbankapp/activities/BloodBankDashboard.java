package com.example.bloodbankapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bloodbankapp.R;


import com.example.bloodbankapp.database.DatabaseHelper;
import com.example.bloodbankapp.utils.SessionManager;
import java.util.HashMap;

public class BloodBankDashboard extends AppCompatActivity {

    private Button btnLogout;
    private TextView tvTotalUnits, tvPendingRequests;
    private SessionManager sessionManager;


    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloodbank_dashboard);

        sessionManager = new SessionManager(this);

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


    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
    }

    private void loadDashboardData() {

        HashMap<String, Integer> bloodData = dbHelper.getBloodGroupCounts();
        int totalUnits = 0;
        for (int units : bloodData.values()) {
            totalUnits += units;
        }

        int pendingRequests = dbHelper.countPendingRequests();

        tvTotalUnits.setText(String.valueOf(totalUnits));
        tvPendingRequests.setText(String.valueOf(pendingRequests));

    }
}
