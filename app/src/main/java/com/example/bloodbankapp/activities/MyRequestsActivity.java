package com.example.bloodbankapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bloodbankapp.R;
import com.example.bloodbankapp.adapters.MyRequestAdapter;

// ✅ BƯỚC 1: XÓA IMPORT SAI, THÊM IMPORT DATABASEHELPER
import com.example.bloodbankapp.database.DatabaseHelper;

import com.example.bloodbankapp.models.Request;
import com.example.bloodbankapp.utils.SessionManager;
import java.util.List;

public class MyRequestsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewRequests;
    private MyRequestAdapter myRequestAdapter;
    private SessionManager sessionManager;
    private TextView tvNoRequests;

    // ✅ BƯỚC 2: KHAI BÁO DATABASEHELPER, BỎ REQUESTDAO
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_requests);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Requests");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerViewRequests = findViewById(R.id.rv_my_requests);
        tvNoRequests = findViewById(R.id.tv_no_requests);

        // ✅ BƯỚC 3: KHỞI TẠO DATABASEHELPER
        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        recyclerViewRequests.setLayoutManager(new LinearLayoutManager(this));
        // Không cần gọi loadRequests() ở đây vì onResume sẽ gọi
    }

    private void loadRequests() {
        int userId = sessionManager.getUserId();
        // ✅ BƯỚC 4: LẤY DỮ LIỆU TỪ DATABASEHELPER
        List<Request> requests = dbHelper.getRequestsByUserId(userId);

        if (requests.isEmpty()) {
            recyclerViewRequests.setVisibility(View.GONE);
            tvNoRequests.setVisibility(View.VISIBLE);
        } else {
            recyclerViewRequests.setVisibility(View.VISIBLE);
            tvNoRequests.setVisibility(View.GONE);
            // Khởi tạo và sử dụng MyRequestAdapter
            myRequestAdapter = new MyRequestAdapter(this, requests);
            recyclerViewRequests.setAdapter(myRequestAdapter);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Tải lại dữ liệu mỗi khi quay lại màn hình để đảm bảo nó luôn mới
        loadRequests();
    }
}
