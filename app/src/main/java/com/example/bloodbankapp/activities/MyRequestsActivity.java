package com.example.bloodbankapp.activities;

import android.os.Bundle;
import android.util.Log; // Import Log
import android.view.View;
import android.widget.ProgressBar; // Import ProgressBar (tùy chọn)
import android.widget.TextView;
import android.widget.Toast; // Import Toast
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bloodbankapp.R;
import com.example.bloodbankapp.adapters.MyRequestAdapter; // Sử dụng adapter này
// import com.example.bloodbankapp.database.DatabaseHelper; // ❌ Không dùng SQLite nữa
import com.example.bloodbankapp.models.Request;
import com.example.bloodbankapp.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;   // ✅ Import FirebaseAuth
import com.google.firebase.auth.FirebaseUser;   // ✅ Import FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore; // ✅ Import Firestore
import com.google.firebase.firestore.Query;           // ✅ Import Query
import com.google.firebase.firestore.QueryDocumentSnapshot; // ✅ Import QueryDocumentSnapshot

import java.util.ArrayList; // Import ArrayList
import java.util.List;

public class MyRequestsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewRequests;
    private MyRequestAdapter myRequestAdapter; // ✅ Sử dụng MyRequestAdapter
    private SessionManager sessionManager;
    private TextView tvNoRequests;
    private ProgressBar progressBar; // Tùy chọn: ProgressBar

    // private DatabaseHelper dbHelper; // ❌
    private FirebaseFirestore dbFirestore; // ✅ Khai báo Firestore
    private FirebaseAuth mAuth; // ✅ Khai báo FirebaseAuth
    private List<Request> requestList; // ✅ Danh sách để lưu dữ liệu từ Firestore

    private static final String TAG = "MyRequestsActivity"; // Thêm TAG

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_requests); // Đảm bảo layout có ProgressBar nếu dùng

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Requests");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        // Xử lý nút back trên toolbar
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerViewRequests = findViewById(R.id.rv_my_requests);
        tvNoRequests = findViewById(R.id.tv_no_requests);
        // progressBar = findViewById(R.id.progressBar); // ✅ Ánh xạ ProgressBar nếu có trong layout

        // dbHelper = new DatabaseHelper(this); // ❌
        dbFirestore = FirebaseFirestore.getInstance(); // ✅ Khởi tạo Firestore
        sessionManager = new SessionManager(this);
        mAuth = FirebaseAuth.getInstance(); // ✅ Khởi tạo FirebaseAuth
        requestList = new ArrayList<>(); // ✅ Khởi tạo danh sách

        setupRecyclerView();
        // Load data trong onResume
    }

    private void setupRecyclerView() {
        recyclerViewRequests.setLayoutManager(new LinearLayoutManager(this));
        // Khởi tạo adapter với danh sách rỗng ban đầu
        myRequestAdapter = new MyRequestAdapter(this, requestList);
        recyclerViewRequests.setAdapter(myRequestAdapter);
    }

    // ✅ Hàm load requests từ Firestore
    private void loadRequestsFromFirestore() {
        FirebaseUser currentUserAuth = mAuth.getCurrentUser();
        if (currentUserAuth == null) {
            Log.w(TAG, "Cannot load requests: User not logged in.");
            Toast.makeText(this, "Please log in again.", Toast.LENGTH_SHORT).show();
            // Optional: Redirect to login
            return;
        }
        String currentUserUid = currentUserAuth.getUid();

        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        recyclerViewRequests.setVisibility(View.GONE); // Ẩn list
        tvNoRequests.setVisibility(View.GONE); // Ẩn thông báo rỗng

        dbFirestore.collection("requests")
                .whereEqualTo("requesterUid", currentUserUid) // Lọc theo UID người dùng
                .orderBy("requestTimestamp", Query.Direction.DESCENDING) // Sắp xếp mới nhất trước
                .get()
                .addOnCompleteListener(task -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        requestList.clear(); // Xóa dữ liệu cũ
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                try {
                                    Request request = document.toObject(Request.class);
                                    request.setDocumentId(document.getId()); // Lưu ID Firestore
                                    requestList.add(request);
                                } catch (Exception e) {
                                    Log.e(TAG, "Error converting request document " + document.getId(), e);
                                }
                            }
                        }

                        // Cập nhật adapter và hiển thị/ẩn view
                        myRequestAdapter.notifyDataSetChanged();
                        if (requestList.isEmpty()) {
                            recyclerViewRequests.setVisibility(View.GONE);
                            tvNoRequests.setVisibility(View.VISIBLE);
                            Log.d(TAG, "No requests found for user " + currentUserUid);
                        } else {
                            recyclerViewRequests.setVisibility(View.VISIBLE);
                            tvNoRequests.setVisibility(View.GONE);
                        }
                    } else {
                        Log.w(TAG, "Error getting documents: ", task.getException());
                        Toast.makeText(MyRequestsActivity.this, "Error loading requests.", Toast.LENGTH_SHORT).show();
                        recyclerViewRequests.setVisibility(View.GONE);
                        tvNoRequests.setVisibility(View.VISIBLE); // Hiển thị lỗi/rỗng
                    }
                });

        /* // ❌ Xóa phần đọc từ SQLite
        int userId = sessionManager.getUserId();
        List<Request> requests = dbHelper.getRequestsByUserId(userId);
        if (requests.isEmpty()) { ... } else { ... }
        */
    }

    // Không cần onSupportNavigateUp nữa vì đã xử lý trong onCreate
    /*
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    */

    @Override
    protected void onResume() {
        super.onResume();
        // Tải lại dữ liệu từ Firestore mỗi khi quay lại màn hình
        loadRequestsFromFirestore();
    }
}