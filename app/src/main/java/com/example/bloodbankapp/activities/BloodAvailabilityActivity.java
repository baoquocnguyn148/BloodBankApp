package com.example.bloodbankapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bloodbankapp.R;
import com.example.bloodbankapp.adapters.BloodAvailabilityAdapter;
import com.example.bloodbankapp.database.DatabaseHelper;
import com.example.bloodbankapp.models.BloodUnit;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;
import java.util.Locale;

public class BloodAvailabilityActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RecyclerView recyclerView;
    private TextView tvTotalUnits;
    private TextView tvNoInventoryFound;

    private BloodAvailabilityAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_availability);

        dbHelper = new DatabaseHelper(this);

        // Ánh xạ các view từ file layout của bạn
        toolbar = findViewById(R.id.toolbar);
        // ID của RecyclerView trong layout của bạn là 'rv_blood_inventory'
        recyclerView = findViewById(R.id.rv_blood_inventory);
        tvTotalUnits = findViewById(R.id.tv_total_units);
        tvNoInventoryFound = findViewById(R.id.tv_no_inventory_found);

        setupToolbar();
        setupRecyclerView();
        loadAndDisplayInventory();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Xử lý khi nhấn nút Back trên Toolbar
        onBackPressed();
        return true;
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Không cho phép RecyclerView tự cuộn bên trong NestedScrollView
        recyclerView.setNestedScrollingEnabled(false);
    }

    private void loadAndDisplayInventory() {
        // Giả sử dbHelper.getAllBloodUnits() trả về List<BloodUnit>
        List<BloodUnit> unitList = dbHelper.getAllInventory();


        if (unitList == null || unitList.isEmpty()) {
            // Hiển thị thông báo không có dữ liệu
            recyclerView.setVisibility(View.GONE);
            tvNoInventoryFound.setVisibility(View.VISIBLE);
            tvTotalUnits.setText("Total Units Available: 0");
        } else {
            // Ẩn thông báo và hiển thị RecyclerView
            recyclerView.setVisibility(View.VISIBLE);
            tvNoInventoryFound.setVisibility(View.GONE);

            // Tính toán và hiển thị tổng số đơn vị máu
            int totalUnits = 0;
            for (BloodUnit unit : unitList) {
                totalUnits += unit.getUnits();
            }
            tvTotalUnits.setText(String.format(Locale.getDefault(), "Total Units Available: %d", totalUnits));

            adapter = new BloodAvailabilityAdapter(this, unitList);
            recyclerView.setAdapter(adapter);
        }
    }
}
