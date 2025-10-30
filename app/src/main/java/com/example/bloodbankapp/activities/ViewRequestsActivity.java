package com.example.bloodbankapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar; // Import ProgressBar (nếu cần)
import android.widget.TextView; // ✅ Import TextView
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bloodbankapp.R;
import com.example.bloodbankapp.adapters.BloodRequestAdapter; // Adapter này cần được sửa sau
import com.example.bloodbankapp.models.Request;
// import com.example.bloodbankapp.database.DatabaseHelper; // ❌ Không dùng SQLite nữa
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewRequestsActivity extends AppCompatActivity {

    private RecyclerView rvBloodRequests;
    private BloodRequestAdapter adapter;
    private List<Request> requestList;
    private Toolbar toolbar;
    private ProgressBar progressBar; // Tùy chọn: Thêm ProgressBar
    private TextView tvNoRequestsFound; // ✅ Thêm TextView báo rỗng

    // private DatabaseHelper dbHelper; // ❌
    private FirebaseFirestore dbFirestore; // ✅ Khai báo Firestore

    private static final String TAG = "ViewRequestsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requests); // Đảm bảo layout có ProgressBar và TextView nếu dùng

        toolbar = findViewById(R.id.toolbar_view_requests);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("All Blood Requests");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rvBloodRequests = findViewById(R.id.rv_blood_requests);
        // progressBar = findViewById(R.id.progressBar); // ✅ Ánh xạ ProgressBar nếu có
        tvNoRequestsFound = findViewById(R.id.tv_no_requests); // ✅ Ánh xạ TextView báo rỗng (Thêm ID này vào layout nếu chưa có)

        rvBloodRequests.setLayoutManager(new LinearLayoutManager(this));

        // dbHelper = new DatabaseHelper(this); // ❌
        dbFirestore = FirebaseFirestore.getInstance(); // ✅ Khởi tạo Firestore
        requestList = new ArrayList<>();

        // Adapter giờ đã sẵn sàng để nhận documentId
        adapter = new BloodRequestAdapter(this, requestList);
        rvBloodRequests.setAdapter(adapter);

        loadDataFromFirestore();
    }


    private void loadDataFromFirestore() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        rvBloodRequests.setVisibility(View.GONE); // Ẩn danh sách
        if (tvNoRequestsFound != null) tvNoRequestsFound.setVisibility(View.GONE); // Ẩn thông báo rỗng

        dbFirestore.collection("requests")
                .orderBy("requestTimestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        requestList.clear();
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                try {
                                    Request request = document.toObject(Request.class);
                                    // ✅ Gán ID của document Firestore vào đối tượng Request
                                    request.setDocumentId(document.getId());
                                    requestList.add(request);
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                } catch (Exception e) {
                                    Log.e(TAG, "Error converting document " + document.getId(), e);
                                    // Bỏ qua document bị lỗi và tiếp tục
                                }
                            }
                            adapter.notifyDataSetChanged();

                            // Hiển thị RecyclerView nếu có dữ liệu
                            if (!requestList.isEmpty()) {
                                rvBloodRequests.setVisibility(View.VISIBLE);
                            } else {
                                // Hiển thị thông báo rỗng nếu list rỗng sau khi xử lý
                                if (tvNoRequestsFound != null) tvNoRequestsFound.setVisibility(View.VISIBLE);
                                Toast.makeText(this, "No valid blood requests found.", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Log.d(TAG, "No requests found.");
                            if (tvNoRequestsFound != null) tvNoRequestsFound.setVisibility(View.VISIBLE); // Hiển thị thông báo rỗng
                            Toast.makeText(this, "No blood requests found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                        if (tvNoRequestsFound != null) tvNoRequestsFound.setVisibility(View.VISIBLE); // Hiển thị thông báo rỗng khi lỗi
                        Toast.makeText(this, "Error loading requests.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}