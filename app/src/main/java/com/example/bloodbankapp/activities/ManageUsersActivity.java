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
import com.example.bloodbankapp.adapters.UserAdapter;
// import com.example.bloodbankapp.database.DatabaseHelper; // ❌ Không dùng SQLite nữa
import com.example.bloodbankapp.models.User;
import com.google.firebase.firestore.FirebaseFirestore; // ✅ Import Firestore
import com.google.firebase.firestore.Query;           // ✅ Import Query
import com.google.firebase.firestore.QueryDocumentSnapshot; // ✅ Import QueryDocumentSnapshot

import java.util.ArrayList;
import java.util.List;

public class ManageUsersActivity extends AppCompatActivity {

    private RecyclerView recyclerViewUsers;
    private UserAdapter userAdapter;
    private List<User> userList = new ArrayList<>();
    // private DatabaseHelper dbHelper; // ❌ Không dùng SQLite nữa
    private FirebaseFirestore dbFirestore; // ✅ Khai báo Firestore
    private Toolbar toolbar;
    private TextView tvEmptyUsers;
    private ProgressBar progressBar; // Tùy chọn: ProgressBar

    private static final String TAG = "ManageUsersActivity"; // Thêm TAG

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users); // Đảm bảo layout có ProgressBar nếu dùng

        // dbHelper = new DatabaseHelper(this); // ❌
        dbFirestore = FirebaseFirestore.getInstance(); // ✅ Khởi tạo Firestore

        initViews();
        setupToolbar();
        setupRecyclerView();

        // Load data trong onResume
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsersDataFromFirestore(); // ✅ Đổi tên hàm và gọi hàm mới
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_manage_users);
        recyclerViewUsers = findViewById(R.id.recycler_view_users);
        tvEmptyUsers = findViewById(R.id.tv_empty_users);
        // progressBar = findViewById(R.id.progressBar); // ✅ Ánh xạ ProgressBar nếu có trong layout
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Manage Users");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        // Khởi tạo adapter với list rỗng ban đầu
        userAdapter = new UserAdapter(this, userList);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUsers.setAdapter(userAdapter);
    }

    // ✅ Hàm mới để load dữ liệu từ Firestore
    private void loadUsersDataFromFirestore() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE); // Hiển thị loading
        recyclerViewUsers.setVisibility(View.GONE); // Ẩn danh sách
        tvEmptyUsers.setVisibility(View.GONE); // Ẩn thông báo rỗng

        dbFirestore.collection("users") // Truy cập collection "users"
                .orderBy("name", Query.Direction.ASCENDING) // Sắp xếp theo tên (tùy chọn)
                .get() // Lấy dữ liệu một lần
                .addOnCompleteListener(task -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE); // Ẩn loading

                    if (task.isSuccessful()) {
                        userList.clear(); // Xóa dữ liệu cũ
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                try {
                                    User user = document.toObject(User.class);
                                    // Gán Firestore Document ID vào User object nếu cần (ví dụ: để xóa/sửa sau này)
                                    // Bạn cần thêm trường documentId vào model User và setter tương ứng
                                    // user.setDocumentId(document.getId());
                                    userList.add(user);
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                } catch (Exception e) {
                                    Log.e(TAG, "Error converting user document " + document.getId(), e);
                                }
                            }
                        }

                        // Cập nhật adapter và hiển thị/ẩn view
                        userAdapter.notifyDataSetChanged();
                        if (userList.isEmpty()) {
                            recyclerViewUsers.setVisibility(View.GONE);
                            tvEmptyUsers.setVisibility(View.VISIBLE);
                            Log.d(TAG, "No users found in Firestore.");
                        } else {
                            recyclerViewUsers.setVisibility(View.VISIBLE);
                            tvEmptyUsers.setVisibility(View.GONE);
                        }
                    } else {
                        Log.w(TAG, "Error getting user documents.", task.getException());
                        Toast.makeText(ManageUsersActivity.this, "Error loading users.", Toast.LENGTH_SHORT).show();
                        recyclerViewUsers.setVisibility(View.GONE);
                        tvEmptyUsers.setVisibility(View.VISIBLE); // Hiển thị lỗi/rỗng
                    }
                });

        /* // ❌ Xóa code đọc từ SQLite
        List<User> allUsers = dbHelper.getAllUsers();
        userList.clear();
        userList.addAll(allUsers);
        userAdapter.notifyDataSetChanged();
        if (userList.isEmpty()) { ... } else { ... }
        */
    }
}