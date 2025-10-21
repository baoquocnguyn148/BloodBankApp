package com.example.bloodbankapp.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;import com.example.bloodbankapp.activities.HomeActivity;
import java.util.HashMap;

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

    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }

    public String getUserName() {
        return pref.getString(KEY_NAME, null);
    }

    public String getUserRole() {
        return pref.getString(KEY_ROLE, null);
    }

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


    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }


    public void logoutUser() {
        editor.clear();
        editor.commit();
        Intent i = new Intent(context, HomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(i);
    }
}
