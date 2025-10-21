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
import com.example.bloodbankapp.adapters.BloodAvailabilityAdapter;
import com.example.bloodbankapp.database.DatabaseHelper;
import com.example.bloodbankapp.models.BloodUnit;
import com.example.bloodbankapp.utils.SessionManager;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BloodAvailabilityActivity extends AppCompatActivity {

    private RecyclerView rvBloodInventory;
    private BloodAvailabilityAdapter bloodAvailabilityAdapter;
    private List<BloodUnit> bloodInventoryList;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    
    private TextView tvTotalUnits, tvNoInventoryFound;
    private MaterialCardView cardBloodSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_availability);

        // Initialize helpers
        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Please log in to view blood availability", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupRecyclerView();
        loadBloodInventory();
    }

    private void initViews() {
        rvBloodInventory = findViewById(R.id.rv_blood_inventory);
        tvTotalUnits = findViewById(R.id.tv_total_units);
        tvNoInventoryFound = findViewById(R.id.tv_no_inventory_found);
        cardBloodSummary = findViewById(R.id.card_blood_summary);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Blood Availability");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        bloodInventoryList = new ArrayList<>();
        bloodAvailabilityAdapter = new BloodAvailabilityAdapter(this, bloodInventoryList);
        rvBloodInventory.setLayoutManager(new LinearLayoutManager(this));
        rvBloodInventory.setAdapter(bloodAvailabilityAdapter);
        rvBloodInventory.setHasFixedSize(true);
    }

    private void loadBloodInventory() {
        // Get blood inventory from database
        List<BloodUnit> inventory = dbHelper.getAllInventory();
        
        if (inventory != null && !inventory.isEmpty()) {
            bloodInventoryList.clear();
            bloodInventoryList.addAll(inventory);
            bloodAvailabilityAdapter.notifyDataSetChanged();
            
            // Show inventory
            rvBloodInventory.setVisibility(View.VISIBLE);
            tvNoInventoryFound.setVisibility(View.GONE);
            cardBloodSummary.setVisibility(View.VISIBLE);
            
            // Calculate and display total units
            int totalUnits = 0;
            for (BloodUnit unit : inventory) {
                totalUnits += unit.getUnits();
            }
            tvTotalUnits.setText("Total Units Available: " + totalUnits);
            
        } else {
            // No inventory found
            rvBloodInventory.setVisibility(View.GONE);
            tvNoInventoryFound.setVisibility(View.VISIBLE);
            cardBloodSummary.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to this screen
        loadBloodInventory();
    }
}
