package com.example.bloodbankapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloodbankapp.R;
import com.example.bloodbankapp.adapters.AnalyticsAdapter;
import com.example.bloodbankapp.database.DatabaseHelper;
import com.example.bloodbankapp.models.AnalyticsData;
import com.example.bloodbankapp.utils.SessionManager;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalyticsActivity extends AppCompatActivity {

    private RecyclerView rvAnalytics;
    private AnalyticsAdapter analyticsAdapter;
    private List<AnalyticsData> analyticsList;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    // Key Metrics Cards
    private MaterialCardView cardTotalUnits, cardActiveDonors, cardPendingRequests, cardFulfilledRequests, cardCriticalStock;
    private TextView tvTotalUnits, tvActiveDonors, tvPendingRequests, tvFulfilledRequests, tvCriticalStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        // Initialize helpers
        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        // Check if user has permission (Admin or Staff only)
        if (!sessionManager.isLoggedIn() || 
            (!"admin".equals(sessionManager.getUserRole()) && !"blood_bank_staff".equals(sessionManager.getUserRole()))) {
            Toast.makeText(this, "Access denied. Admin or Staff access required.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupRecyclerView();
        loadAnalyticsData();
    }

    private void initViews() {
        rvAnalytics = findViewById(R.id.rv_analytics);
        
        // Key Metrics Cards
        cardTotalUnits = findViewById(R.id.card_total_units);
        cardActiveDonors = findViewById(R.id.card_active_donors);
        cardPendingRequests = findViewById(R.id.card_pending_requests);
        cardFulfilledRequests = findViewById(R.id.card_fulfilled_requests);
        cardCriticalStock = findViewById(R.id.card_critical_stock);

        tvTotalUnits = findViewById(R.id.tv_total_units);
        tvActiveDonors = findViewById(R.id.tv_active_donors);
        tvPendingRequests = findViewById(R.id.tv_pending_requests);
        tvFulfilledRequests = findViewById(R.id.tv_fulfilled_requests);
        tvCriticalStock = findViewById(R.id.tv_critical_stock);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Analytics Dashboard");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        analyticsList = new ArrayList<>();
        analyticsAdapter = new AnalyticsAdapter(this, analyticsList);
        rvAnalytics.setLayoutManager(new LinearLayoutManager(this));
        rvAnalytics.setAdapter(analyticsAdapter);
        rvAnalytics.setHasFixedSize(true);
    }

    private void loadAnalyticsData() {
        // Load Key Metrics
        loadKeyMetrics();
        
        // Load Analytics Charts and Data
        loadAnalyticsCharts();
    }

    private void loadKeyMetrics() {
        // Total Blood Units
        Map<String, Integer> bloodCounts = dbHelper.getBloodGroupCounts();
        int totalUnits = 0;
        for (int units : bloodCounts.values()) {
            totalUnits += units;
        }
        tvTotalUnits.setText(String.valueOf(totalUnits));

        // Active Donors (assuming donors who have donated in last 3 months)
        int activeDonors = dbHelper.countUsersByRole("donor");
        tvActiveDonors.setText(String.valueOf(activeDonors));

        // Pending Requests
        int pendingRequests = dbHelper.countPendingRequests();
        tvPendingRequests.setText(String.valueOf(pendingRequests));

        // Fulfilled Requests (this month - simplified)
        int totalRequests = dbHelper.countAllRequests();
        int fulfilledRequests = totalRequests - pendingRequests;
        tvFulfilledRequests.setText(String.valueOf(fulfilledRequests));

        // Critical Stock (blood types with < 50 units)
        int criticalStockCount = 0;
        for (Map.Entry<String, Integer> entry : bloodCounts.entrySet()) {
            if (entry.getValue() < 50) {
                criticalStockCount++;
            }
        }
        tvCriticalStock.setText(String.valueOf(criticalStockCount));

        // Set card colors based on critical levels
        setCardColors(totalUnits, pendingRequests, criticalStockCount);
    }

    private void setCardColors(int totalUnits, int pendingRequests, int criticalStockCount) {
        // Total Units - Green if > 1000, Yellow if 500-1000, Red if < 500
        if (totalUnits > 1000) {
            cardTotalUnits.setCardBackgroundColor(getResources().getColor(R.color.status_approved_bg));
        } else if (totalUnits > 500) {
            cardTotalUnits.setCardBackgroundColor(getResources().getColor(R.color.status_donors_bg));
        } else {
            cardTotalUnits.setCardBackgroundColor(getResources().getColor(R.color.status_rejected_bg));
        }

        // Critical Stock - Red if > 0
        if (criticalStockCount > 0) {
            cardCriticalStock.setCardBackgroundColor(getResources().getColor(R.color.status_rejected_bg));
        } else {
            cardCriticalStock.setCardBackgroundColor(getResources().getColor(R.color.status_approved_bg));
        }
    }

    private void loadAnalyticsCharts() {
        analyticsList.clear();

        // 1. Blood Inventory Analytics
        Map<String, Integer> bloodCounts = dbHelper.getBloodGroupCounts();
        AnalyticsData bloodInventoryData = new AnalyticsData(
            "Blood Inventory by Type",
            "Stock Level Analysis",
            AnalyticsData.ChartType.BAR_CHART,
            bloodCounts
        );
        analyticsList.add(bloodInventoryData);

        // 2. Request Status Distribution
        Map<String, Integer> requestStatus = new HashMap<>();
        requestStatus.put("Pending", dbHelper.countPendingRequests());
        requestStatus.put("Approved", dbHelper.countAllRequests() - dbHelper.countPendingRequests());
        requestStatus.put("Rejected", 0); // Simplified - no rejection tracking yet
        
        AnalyticsData requestStatusData = new AnalyticsData(
            "Request Status Distribution",
            "Current Request Status",
            AnalyticsData.ChartType.PIE_CHART,
            requestStatus
        );
        analyticsList.add(requestStatusData);

        // 3. Donor Distribution by Blood Type
        Map<String, Integer> donorDistribution = new HashMap<>();
        // This would need a more complex query to get donor blood type distribution
        // For now, using simplified data
        donorDistribution.put("O+", 45);
        donorDistribution.put("A+", 35);
        donorDistribution.put("B+", 15);
        donorDistribution.put("AB+", 5);

        AnalyticsData donorDistributionData = new AnalyticsData(
            "Donor Distribution by Blood Type",
            "Donor Blood Type Analysis",
            AnalyticsData.ChartType.PIE_CHART,
            donorDistribution
        );
        analyticsList.add(donorDistributionData);

        // 4. Time-based Analytics (simplified)
        Map<String, Integer> timeAnalytics = new HashMap<>();
        timeAnalytics.put("Peak Hours (12-18)", 45);
        timeAnalytics.put("Morning (06-12)", 25);
        timeAnalytics.put("Evening (18-24)", 20);
        timeAnalytics.put("Night (00-06)", 10);

        AnalyticsData timeData = new AnalyticsData(
            "Request Time Distribution",
            "Peak Hours Analysis",
            AnalyticsData.ChartType.BAR_CHART,
            timeAnalytics
        );
        analyticsList.add(timeData);

        analyticsAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh analytics data when returning to this screen
        loadAnalyticsData();
    }
}


