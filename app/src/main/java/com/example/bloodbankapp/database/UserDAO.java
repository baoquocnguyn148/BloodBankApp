package com.example.bloodbankapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.bloodbankapp.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserDAO extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bloodbank.db";

    private static final int DATABASE_VERSION = 2;
    public static final String TABLE_USERS = "users";

    public static final String COLUMN_ID = "user_id";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_BLOOD_GROUP = "blood_group";
    public static final String COLUMN_ROLE = "role";

    public UserDAO(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_EMAIL + " TEXT UNIQUE,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_PHONE + " TEXT,"
                + COLUMN_ADDRESS + " TEXT,"
                + COLUMN_BLOOD_GROUP + " TEXT,"
                + COLUMN_ROLE + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        // ✅ TẠO SẴN TÀI KHOẢN ADMIN
        addInitialAdminAccount(db);
    }

    private void addInitialAdminAccount(SQLiteDatabase db) {
        ContentValues adminValues = new ContentValues();
        adminValues.put(COLUMN_EMAIL, "qbaonguyen1408@gmail.com");
        adminValues.put(COLUMN_NAME, "Bao Nguyen (Admin)");
        adminValues.put(COLUMN_PHONE, "N/A");
        adminValues.put(COLUMN_ADDRESS, "N/A");
        adminValues.put(COLUMN_BLOOD_GROUP, "N/A");
        adminValues.put(COLUMN_ROLE, "admin");
        db.insertWithOnConflict(TABLE_USERS, null, adminValues, SQLiteDatabase.CONFLICT_IGNORE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa bảng cũ và tạo lại (kèm theo tài khoản admin)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public long addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_NAME, user.getName());
        values.put(COLUMN_PHONE, user.getPhone());
        values.put(COLUMN_ADDRESS, user.getAddress());
        values.put(COLUMN_BLOOD_GROUP, user.getBloodGroup());
        values.put(COLUMN_ROLE, user.getRole());
        long result = db.insertWithOnConflict(TABLE_USERS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
        return result;
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        Cursor cursor = db.query(TABLE_USERS, null, COLUMN_EMAIL + " = ?", new String[]{email}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BLOOD_GROUP)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE))
            );
            cursor.close();
        }
        db.close();
        return user;
    }

    public int updateUserByEmail(String email, String name, String phone, String address, String bloodGroup) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_ADDRESS, address);
        values.put(COLUMN_BLOOD_GROUP, bloodGroup);
        String selection = COLUMN_EMAIL + " = ?";
        String[] selectionArgs = { email };
        int rowsAffected = db.update(TABLE_USERS, values, selection, selectionArgs);
        db.close();
        return rowsAffected;
    }

    public int countUsersByRole(String role) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_ROLE + " = ?";
        String[] selectionArgs = { role };
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ID}, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS, null);

        if (cursor.moveToFirst()) {
            do {
                User user = new User(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BLOOD_GROUP)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE))
                );
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return userList;
    }

    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(userId) };
        Cursor cursor = db.query(TABLE_USERS, null, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BLOOD_GROUP)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE))
            );
            cursor.close();
        }
        db.close();
        return user;
    }

    public List<User> getDonorsByBloodGroup(String bloodGroup) {
        List<User> donorList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_ROLE + " = ? AND " + COLUMN_BLOOD_GROUP + " = ?";
        String[] selectionArgs = { "donor", bloodGroup };
        Cursor cursor = db.query(TABLE_USERS, null, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                User user = new User(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BLOOD_GROUP)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE))
                );
                donorList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return donorList;
    }
}
