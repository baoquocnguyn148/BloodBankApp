package com.example.bloodbankapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bloodbankapp.R;
import com.example.bloodbankapp.adapters.UserAdapter;
import com.example.bloodbankapp.database.DatabaseHelper;
import com.example.bloodbankapp.models.User;
import java.util.ArrayList;
import java.util.List;

public class ManageUsersActivity extends AppCompatActivity {

    private RecyclerView recyclerViewUsers;
    private UserAdapter userAdapter;
    private List<User> userList = new ArrayList<>();
    private DatabaseHelper dbHelper;
    private Toolbar toolbar;
    private TextView tvEmptyUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        dbHelper = new DatabaseHelper(this);

        initViews();
        setupToolbar();
        setupRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsersData();
    }

    private void initViews() {
        // Các ID này giờ đã khớp với activity_manage_users.xml
        toolbar = findViewById(R.id.toolbar_manage_users);
        recyclerViewUsers = findViewById(R.id.recycler_view_users);
        tvEmptyUsers = findViewById(R.id.tv_empty_users);
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
        userAdapter = new UserAdapter(this, userList);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUsers.setAdapter(userAdapter);
    }

    private void loadUsersData() {
        List<User> allUsers = dbHelper.getAllUsers();
        userList.clear();
        userList.addAll(allUsers);
        userAdapter.notifyDataSetChanged();

        if (userList.isEmpty()) {
            recyclerViewUsers.setVisibility(View.GONE);
            tvEmptyUsers.setVisibility(View.VISIBLE);
        } else {
            recyclerViewUsers.setVisibility(View.VISIBLE);
            tvEmptyUsers.setVisibility(View.GONE);
        }
    }
}
