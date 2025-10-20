package com.example.bloodbankapp.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;import com.example.bloodbankapp.activities.HomeActivity;
import java.util.HashMap;

/**
 * Lớp quản lý phiên đăng nhập của người dùng sử dụng SharedPreferences.
 * Đảm bảo lưu trữ và truy xuất thông tin người dùng một cách nhất quán.
 */
public class SessionManager {
    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;
    private final Context context;

    private static final String PREF_NAME = "BloodBankAppSession";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_NAME = "user_name";
    public static final String KEY_EMAIL = "user_email";
    public static final String KEY_PHONE = "user_phone";
    public static final String KEY_ADDRESS = "user_address";
    public static final String KEY_BLOOD_GROUP = "user_blood_group";
    public static final String KEY_ROLE = "user_role";

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(int userId, String name, String email, String phone, String address, String bloodGroup, String role) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_ADDRESS, address);
        editor.putString(KEY_BLOOD_GROUP, bloodGroup);
        editor.putString(KEY_ROLE, role);
        editor.commit(); // Dùng commit() để đảm bảo an toàn dữ liệu
    }

    // ✅✅✅ BỔ SUNG CÁC HÀM CÒN THIẾU TẠI ĐÂY ✅✅✅

    /**
     * Lấy ID của người dùng hiện tại.
     * @return User ID, hoặc -1 nếu không tìm thấy.
     */
    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }

    /**
     * Lấy tên của người dùng hiện tại.
     * @return Tên người dùng, hoặc null nếu không tìm thấy.
     */
    public String getUserName() {
        return pref.getString(KEY_NAME, null);
    }

    /**
     * Lấy vai trò của người dùng hiện tại.
     * @return Chuỗi vai trò (ví dụ: "admin") hoặc null nếu không có.
     */
    public String getUserRole() {
        return pref.getString(KEY_ROLE, null);
    }

    /**
     * Lấy tất cả thông tin người dùng đã lưu.
     * @return HashMap chứa thông tin người dùng.
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_USER_ID, String.valueOf(pref.getInt(KEY_USER_ID, -1)));
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_PHONE, pref.getString(KEY_PHONE, null));
        user.put(KEY_ADDRESS, pref.getString(KEY_ADDRESS, null));
        user.put(KEY_BLOOD_GROUP, pref.getString(KEY_BLOOD_GROUP, null));
        user.put(KEY_ROLE, pref.getString(KEY_ROLE, null));
        return user;
    }

    /**
     * Kiểm tra xem người dùng đã đăng nhập hay chưa.
     * @return true nếu đã đăng nhập, ngược lại là false.
     */
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    /**
     * Xóa toàn bộ dữ liệu phiên và điều hướng người dùng về màn hình trung chuyển (HomeActivity).
     */
    public void logoutUser() {
        editor.clear();
        editor.commit(); // Dùng commit() để xóa ngay lập tức.

        // Luôn chuyển về HomeActivity khi logout.
        Intent i = new Intent(context, HomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(i);
    }
}
