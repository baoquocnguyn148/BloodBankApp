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

// ✅ BƯỚC 1: XÓA IMPORT SAI, THÊM IMPORT DATABASEHELPER
import com.example.bloodbankapp.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class ViewRequestsActivity extends AppCompatActivity {

    private RecyclerView rvBloodRequests;
    private BloodRequestAdapter adapter;
    private List<Request> requestList;
    private Toolbar toolbar;

    // ✅ BƯỚC 2: KHAI BÁO DATABASEHELPER, BỎ REQUESTDAO
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requests);

        // --- Thiết lập Toolbar ---
        toolbar = findViewById(R.id.toolbar_view_requests);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("All Blood Requests"); // Đặt tiêu đề cho rõ ràng
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // --- Thiết lập RecyclerView ---
        rvBloodRequests = findViewById(R.id.rv_blood_requests);
        rvBloodRequests.setLayoutManager(new LinearLayoutManager(this));

        // --- Khởi tạo và kết nối dữ liệu ---
        // ✅ BƯỚC 3: KHỞI TẠO DATABASEHELPER
        dbHelper = new DatabaseHelper(this);
        requestList = new ArrayList<>();

        // Khởi tạo Adapter với danh sách rỗng ban đầu
        adapter = new BloodRequestAdapter(this, requestList);
        rvBloodRequests.setAdapter(adapter);
    }

    // ✅ BƯỚC 5: SỬ DỤNG ONRESUME ĐỂ DỮ LIỆU LUÔN MỚI
    @Override
    protected void onResume() {
        super.onResume();
        loadDataFromDatabase();
    }

    /**
     * Hàm này sẽ tải tất cả các yêu cầu máu từ database bằng DatabaseHelper
     * và cập nhật giao diện của RecyclerView.
     */
    private void loadDataFromDatabase() {
        // ✅ BƯỚC 4: LẤY DỮ LIỆU TỪ DATABASEHELPER
        // Cần thêm phương thức getAllBloodRequests() vào DatabaseHelper
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
