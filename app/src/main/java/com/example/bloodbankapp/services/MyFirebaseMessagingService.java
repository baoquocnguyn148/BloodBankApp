package com.example.bloodbankapp.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.bloodbankapp.database.DatabaseHelper; // 💡 Import DatabaseHelper
import com.example.bloodbankapp.utils.NotificationHelper;
import com.example.bloodbankapp.utils.SessionManager; // 💡 Import SessionManager
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Được gọi khi có một tin nhắn mới từ Firebase.
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            Log.d(TAG, "Notification Received: Title=" + title + ", Body=" + body);

            // Sử dụng NotificationHelper để hiển thị thông báo
            NotificationHelper notificationHelper = new NotificationHelper(this);
            notificationHelper.showNotification(title, body);
        }
    }

    /**
     * ✅ HÀM MỚI QUAN TRỌNG
     * Được gọi khi Firebase cấp một token mới cho thiết bị.
     * Token này là địa chỉ duy nhất để gửi thông báo đến chính xác thiết bị này.
     */
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed FCM token: " + token);

        // Gửi token này lên server hoặc lưu vào local database
        sendRegistrationToServer(token);
    }

    /**
     * ✅ HÀM MỚI QUAN TRỌNG
     * Lưu token vào cơ sở dữ liệu (bảng Users) cho người dùng đang đăng nhập.
     */
    private void sendRegistrationToServer(String token) {
        SessionManager sessionManager = new SessionManager(this);
        // Chỉ cập nhật token nếu người dùng đang đăng nhập
        if (sessionManager.isLoggedIn()) {
            int userId = sessionManager.getUserId();
            if (userId != -1) {
                DatabaseHelper dbHelper = new DatabaseHelper(this);
                dbHelper.updateUserFcmToken(userId, token);
                Log.d(TAG, "FCM Token for userId " + userId + " updated in local DB.");
            }
        }
    }
}
