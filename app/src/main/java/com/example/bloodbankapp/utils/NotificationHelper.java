package com.example.bloodbankapp.utils;

import android.app.NotificationChannel;import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.bloodbankapp.R;
import com.example.bloodbankapp.activities.HomeActivity;
import com.example.bloodbankapp.database.DatabaseHelper;
import com.example.bloodbankapp.models.User;

import java.util.ArrayList;
import java.util.List;

public class NotificationHelper {

    private final Context mContext;
    private static final String CHANNEL_ID = "blood_bank_channel";
    private static final String CHANNEL_NAME = "Blood Bank Notifications";

    public NotificationHelper(Context context) {
        mContext = context;
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for blood requests and updates");
            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Hàm để hiển thị một thông báo đơn giản.
     * @param title   Tiêu đề của thông báo
     * @param message Nội dung của thông báo
     */
    public void showNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(mContext, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                // ✅✅✅ SỬA LỖI TẠI ĐÂY: SỬ DỤNG ICON CHUẨN CỦA ANDROID ✅✅✅
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent);

        if (notificationManager != null) {
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }

    /**
     * Hàm này giữ nguyên, không cần sửa.
     */
    public void notifyDonors(String requiredBloodGroup, String hospital) {
        DatabaseHelper dbHelper = new DatabaseHelper(mContext);
        List<User> allUsers = dbHelper.getAllUsers();
        List<User> matchingDonors = new ArrayList<>();

        for (User user : allUsers) {
            if ("donor".equalsIgnoreCase(user.getRole()) && requiredBloodGroup.equals(user.getBloodGroup())) {
                matchingDonors.add(user);
            }
        }

        if (matchingDonors.isEmpty()) {
            Log.d("NotificationHelper", "No matching donors found for blood group: " + requiredBloodGroup);
            return;
        }

        String title = "Urgent Blood Request!";
        String message = "Blood type " + requiredBloodGroup + " is needed at " + hospital + ". Can you help?";

        for (User donor : matchingDonors) {
            showNotification(title, "Hi " + donor.getName() + ", " + message);
        }
    }
}
