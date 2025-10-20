package com.example.bloodbankapp.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloodbankapp.R;
// import com.example.bloodbankapp.adapters.DonationHistoryAdapter; // Bạn sẽ tạo Adapter này sau
import com.example.bloodbankapp.database.UserDAO; // ✅ SỬA LỖI: Import đúng UserDAO
import com.example.bloodbankapp.models.User;
// Bạn không cần SessionManager nếu dùng FirebaseAuth để lấy user hiện tại
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DonationHistoryActivity extends AppCompatActivity {

    private RecyclerView rvDonationHistory;
    // private DonationHistoryAdapter adapter; // Sẽ dùng khi có Adapter

    // ✅ SỬA LỖI: Khai báo đúng kiểu dữ liệu UserDAO
    private UserDAO userDAO;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_history);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Donation History");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        rvDonationHistory = findViewById(R.id.rv_donation_history);

        mAuth = FirebaseAuth.getInstance();
        // ✅ SỬA LỖI: Khởi tạo đúng đối tượng UserDAO
        userDAO = new UserDAO(this);

        setupRecyclerView();
        loadDonationHistory();
    }

    private void setupRecyclerView() {
        rvDonationHistory.setLayoutManager(new LinearLayoutManager(this));
        // adapter = new DonationHistoryAdapter(new ArrayList<>());
        // rvDonationHistory.setAdapter(adapter);
    }

    private void loadDonationHistory() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            return;
        }

        // Dòng này bây giờ sẽ hoạt động chính xác vì userDAO là một đối tượng UserDAO
        User currentUser = userDAO.getUserByEmail(firebaseUser.getEmail());

        if (currentUser != null) {
            // Trong tương lai, bạn sẽ lấy danh sách lịch sử hiến máu từ database
            // ví dụ: List<Donation> donationList = donationDAO.getDonationsForUser(currentUser.getId());
            // adapter.submitList(donationList);

            // Hiện tại, RecyclerView sẽ trống, đây là hành vi đúng.
        } else {
            // Xử lý lỗi không tìm thấy data user trong SQLite
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
