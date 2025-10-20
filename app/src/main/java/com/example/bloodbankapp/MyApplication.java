package com.example.bloodbankapp;

import android.app.Application;

import com.google.firebase.FirebaseApp;

/**
 * Lớp Application tùy chỉnh để khởi tạo các thư viện toàn cục.
 * Lớp này sẽ được thực thi đầu tiên khi ứng dụng khởi động.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // ✅ BƯỚC QUAN TRỌNG: Khởi tạo Firebase cho toàn bộ ứng dụng
        FirebaseApp.initializeApp(this);
    }
}
