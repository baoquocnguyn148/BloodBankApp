package com.example.bloodbankapp.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.bloodbankapp.R;

// ✅ SỬA LỖI: Đảm bảo class kế thừa từ AppCompatActivity
public class NotificationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Giả sử bạn đã có file layout tên là activity_notifications.xml
        setContentView(R.layout.activity_notifications);

        // Thiết lập Toolbar với nút back
        Toolbar toolbar = findViewById(R.id.toolbar_notifications); // Giả sử id toolbar là toolbar_notifications
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Notifications");
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed()); // Bắt sự kiện khi nhấn nút back
    }
}
