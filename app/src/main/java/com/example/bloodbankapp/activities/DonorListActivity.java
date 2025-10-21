package com.example.bloodbankapp.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloodbankapp.R;
import com.example.bloodbankapp.adapters.UserAdapter;

import com.example.bloodbankapp.database.DatabaseHelper;
import com.example.bloodbankapp.models.User;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DonorListActivity extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteBloodGroup;
    private Button btnFilter;
    private RecyclerView recyclerViewDonors;
    private UserAdapter userAdapter;

    private DatabaseHelper dbHelper;
    private List<User> donorList; // Danh sách để hiển thị trên RecyclerView
    private List<User> allDonorsMasterList; // Danh sách gốc chứa tất cả donor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_list);


        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Available Donors");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        dbHelper = new DatabaseHelper(this);
        initViews();
        setupBloodGroupFilter();
        setupRecyclerView();

        loadAllDonors(); // Tải tất cả donor ban đầu

        btnFilter.setOnClickListener(v -> filterDonors());
    }

    private void initViews() {
        autoCompleteBloodGroup = findViewById(R.id.autoCompleteBloodGroup);
        btnFilter = findViewById(R.id.btnFilter);
        recyclerViewDonors = findViewById(R.id.recyclerViewDonors);
    }

    private void setupBloodGroupFilter() {

        String[] bloodGroups = {"All", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, bloodGroups);
        autoCompleteBloodGroup.setAdapter(adapter);
        autoCompleteBloodGroup.setText(bloodGroups[0], false);
    }

    private void setupRecyclerView() {
        donorList = new ArrayList<>();
        userAdapter = new UserAdapter(this, donorList); // Giả sử bạn có UserAdapter
        recyclerViewDonors.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDonors.setAdapter(userAdapter);
    }

    private void loadAllDonors() {

        allDonorsMasterList = dbHelper.getAllDonorsByRole("donor");


        filterDonors();
    }

    private void filterDonors() {
        String selectedBloodGroup = autoCompleteBloodGroup.getText().toString();
        donorList.clear();

        if (selectedBloodGroup.equalsIgnoreCase("All") || selectedBloodGroup.isEmpty()) {
            donorList.addAll(allDonorsMasterList); // Hiển thị tất cả donor
        } else {
            // Sử dụng Stream API cho code ngắn gọn và hiệu quả hơn
            List<User> filteredList = allDonorsMasterList.stream()
                    .filter(donor -> selectedBloodGroup.equalsIgnoreCase(donor.getBloodGroup()))
                    .collect(Collectors.toList());
            donorList.addAll(filteredList);
        }
        userAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Xử lý sự kiện click nút quay lại trên Toolbar
        finish();
        return true;
    }
}
