package com.example.bloodbankapp.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bloodbankapp.R;
import com.example.bloodbankapp.adapters.BloodRequestAdapter;
import com.example.bloodbankapp.models.Request;


import com.example.bloodbankapp.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class ViewRequestsActivity extends AppCompatActivity {

    private RecyclerView rvBloodRequests;
    private BloodRequestAdapter adapter;
    private List<Request> requestList;
    private Toolbar toolbar;


    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requests);


        toolbar = findViewById(R.id.toolbar_view_requests);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("All Blood Requests"); // Đặt tiêu đề cho rõ ràng
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rvBloodRequests = findViewById(R.id.rv_blood_requests);
        rvBloodRequests.setLayoutManager(new LinearLayoutManager(this));


        dbHelper = new DatabaseHelper(this);
        requestList = new ArrayList<>();


        adapter = new BloodRequestAdapter(this, requestList);
        rvBloodRequests.setAdapter(adapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadDataFromDatabase();
    }

    private void loadDataFromDatabase() {

        List<Request> dataFromDb = dbHelper.getAllBloodRequests();

        if (dataFromDb != null && !dataFromDb.isEmpty()) {
            requestList.clear();
            requestList.addAll(dataFromDb);
        } else {
            Toast.makeText(this, "No blood requests found.", Toast.LENGTH_SHORT).show();
            requestList.clear(); // Đảm bảo danh sách rỗng nếu không có dữ liệu
        }

        // Thông báo cho adapter rằng dữ liệu đã thay đổi
        adapter.notifyDataSetChanged();
    }
}
