package com.example.bloodbankapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.bloodbankapp.models.BloodUnit;
import com.example.bloodbankapp.models.Request;
import com.example.bloodbankapp.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bloodbank_unified.db";
    // ✅ BƯỚC 1: Tăng phiên bản DB sau khi thay đổi cấu trúc bảng
    private static final int DATABASE_VERSION = 2;

    // --- Bảng USERS ---
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PASSWORD = "password";
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_PHONE = "phone";
    public static final String COLUMN_USER_ADDRESS = "address";
    public static final String COLUMN_USER_BLOOD_GROUP = "blood_group";
    public static final String COLUMN_USER_ROLE = "role";
    // ✅ BƯỚC 2: Khai báo tên cột mới cho FCM Token
    public static final String COLUMN_USER_FCM_TOKEN = "fcm_token";

    // --- Bảng REQUESTS ---
    public static final String TABLE_REQUESTS = "requests";
    public static final String COLUMN_REQUEST_ID = "request_id";
    public static final String COLUMN_REQUEST_RECIPIENT_ID = "recipient_id";
    public static final String COLUMN_REQUEST_PATIENT_NAME = "patient_name";
    public static final String COLUMN_REQUEST_HOSPITAL = "hospital";
    public static final String COLUMN_REQUEST_BLOOD_GROUP = "request_blood_group";
    public static final String COLUMN_REQUEST_UNITS = "units";
    public static final String COLUMN_REQUEST_DATE = "request_date";
    public static final String COLUMN_REQUEST_STATUS = "status";

    // --- Bảng INVENTORY ---
    public static final String TABLE_INVENTORY = "blood_inventory";
    public static final String COLUMN_INVENTORY_ID = "inventory_id";
    public static final String COLUMN_INVENTORY_BLOOD_GROUP = "inventory_blood_group";
    public static final String COLUMN_INVENTORY_UNITS = "units";
    public static final String COLUMN_INVENTORY_EXPIRY_DATE = "expiry_date";
    public static final String COLUMN_INVENTORY_DONOR_ID = "donor_id";


    // ✅ BƯỚC 3: Cập nhật câu lệnh tạo bảng Users
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USER_EMAIL + " TEXT UNIQUE NOT NULL,"
            + COLUMN_USER_PASSWORD + " TEXT,"
            + COLUMN_USER_NAME + " TEXT,"
            + COLUMN_USER_PHONE + " TEXT,"
            + COLUMN_USER_ADDRESS + " TEXT,"
            + COLUMN_USER_BLOOD_GROUP + " TEXT,"
            + COLUMN_USER_ROLE + " TEXT,"
            + COLUMN_USER_FCM_TOKEN + " TEXT" + ")"; // Thêm cột fcm_token

    private static final String CREATE_TABLE_REQUESTS = "CREATE TABLE " + TABLE_REQUESTS + "("
            + COLUMN_REQUEST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_REQUEST_RECIPIENT_ID + " INTEGER NOT NULL,"
            + COLUMN_REQUEST_PATIENT_NAME + " TEXT NOT NULL,"
            + COLUMN_REQUEST_HOSPITAL + " TEXT,"
            + COLUMN_REQUEST_BLOOD_GROUP + " TEXT NOT NULL,"
            + COLUMN_REQUEST_UNITS + " INTEGER NOT NULL,"
            + COLUMN_REQUEST_DATE + " TEXT,"
            + COLUMN_REQUEST_STATUS + " TEXT NOT NULL" + ")";

    private static final String CREATE_TABLE_INVENTORY = "CREATE TABLE " + TABLE_INVENTORY + "("
            + COLUMN_INVENTORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_INVENTORY_BLOOD_GROUP + " TEXT NOT NULL,"
            + COLUMN_INVENTORY_UNITS + " INTEGER NOT NULL,"
            + COLUMN_INVENTORY_EXPIRY_DATE + " TEXT,"
            + COLUMN_INVENTORY_DONOR_ID + " INTEGER" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_REQUESTS);
        db.execSQL(CREATE_TABLE_INVENTORY);
        addInitialAdminAccount(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Chiến lược đơn giản: xóa và tạo lại. Dữ liệu cũ sẽ mất.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REQUESTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVENTORY);
        onCreate(db);
    }

    private void addInitialAdminAccount(SQLiteDatabase db) {
        ContentValues adminValues = new ContentValues();
        adminValues.put(COLUMN_USER_EMAIL, "qbaonguyen1408@gmail.com");
        adminValues.put(COLUMN_USER_NAME, "Bao Nguyen (Admin)");
        adminValues.put(COLUMN_USER_ROLE, "admin");
        db.insertWithOnConflict(TABLE_USERS, null, adminValues, SQLiteDatabase.CONFLICT_IGNORE);
    }

    // =====================================================================
    // HÀM CHO BẢNG USERS
    // =====================================================================

    // ✅ BƯỚC 4: Thêm hàm mới để cập nhật FCM Token
    /**
     * Cập nhật lại FCM token cho một người dùng cụ thể.
     * @param userId ID của người dùng cần cập nhật.
     * @param fcmToken Token mới từ Firebase.
     */
    public void updateUserFcmToken(int userId, String fcmToken) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_FCM_TOKEN, fcmToken);

        try {
            int rowsAffected = db.update(TABLE_USERS, values, COLUMN_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)});

            if (rowsAffected > 0) {
                Log.d("DatabaseHelper", "FCM Token updated successfully for userId: " + userId);
            } else {
                Log.w("DatabaseHelper", "Failed to update FCM Token for userId: " + userId + ". User not found?");
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error updating FCM Token", e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }


    public long addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_PHONE, user.getPhone());
        values.put(COLUMN_USER_ADDRESS, user.getAddress());
        values.put(COLUMN_USER_BLOOD_GROUP, user.getBloodGroup());
        values.put(COLUMN_USER_ROLE, user.getRole());
        long id = -1;
        try {
            id = db.insertWithOnConflict(TABLE_USERS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error adding user", e);
        } finally {
            db.close();
        }
        return id;
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        try (Cursor cursor = db.query(TABLE_USERS, null, COLUMN_USER_EMAIL + " = ?", new String[]{email}, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                user = new User(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PHONE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ADDRESS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_BLOOD_GROUP)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ROLE))
                );
                // Bạn có thể thêm lấy fcm_token ở đây nếu cần
                // user.setFcmToken(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_FCM_TOKEN)));
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting user by email", e);
        }
        return user;
    }

    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        try (Cursor cursor = db.query(TABLE_USERS, null, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)}, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                user = new User(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PHONE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ADDRESS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_BLOOD_GROUP)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ROLE))
                );
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting user by ID", e);
        }
        return user;
    }

    // ... (tất cả các hàm khác của bạn giữ nguyên, không cần sửa)
    // ...
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS, null)) {
            if (cursor.moveToFirst()) {
                do {
                    User user = new User(
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PHONE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ADDRESS)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_BLOOD_GROUP)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ROLE))
                    );
                    userList.add(user);
                } while (cursor.moveToNext());
            }
        }
        return userList;
    }

    public List<User> getAllDonorsByRole(String role) {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_USERS, null, COLUMN_USER_ROLE + " = ?", new String[]{role}, null, null, null)) {
            if (cursor.moveToFirst()) {
                do {
                    User user = new User(
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PHONE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ADDRESS)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_BLOOD_GROUP)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ROLE))
                    );
                    userList.add(user);
                } while (cursor.moveToNext());
            }
        }
        return userList;
    }

    public int updateUser(int userId, String name, String phone, String address, String bloodGroup, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, name);
        values.put(COLUMN_USER_PHONE, phone);
        values.put(COLUMN_USER_ADDRESS, address);
        values.put(COLUMN_USER_BLOOD_GROUP, bloodGroup);
        values.put(COLUMN_USER_ROLE, role);
        return db.update(TABLE_USERS, values, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
    }

    public void deleteUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
    }

    public int countUsersByRole(String role) {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;
        try (Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_ROLE + " = ?", new String[]{role})) {
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error counting users by role: " + e.getMessage());
        }
        return count;
    }

    // =====================================================================
    // HÀM CHO BẢNG REQUESTS
    // =====================================================================

    public boolean addBloodRequest(int recipientId, String patientName, String hospital, String bloodGroup, int units, String status, String requestDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_REQUEST_RECIPIENT_ID, recipientId);
        values.put(COLUMN_REQUEST_PATIENT_NAME, patientName);
        values.put(COLUMN_REQUEST_HOSPITAL, hospital);
        values.put(COLUMN_REQUEST_BLOOD_GROUP, bloodGroup);
        values.put(COLUMN_REQUEST_UNITS, units);
        values.put(COLUMN_REQUEST_STATUS, status);
        values.put(COLUMN_REQUEST_DATE, requestDate);
        long result = db.insert(TABLE_REQUESTS, null, values);
        return result != -1;
    }

    public List<Request> getRequestsByUserId(int userId) {
        List<Request> requestList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_REQUESTS, null, COLUMN_REQUEST_RECIPIENT_ID + " = ?",
                new String[]{String.valueOf(userId)}, null, null, COLUMN_REQUEST_ID + " DESC")) {
            if (cursor.moveToFirst()) {
                do {
                    Request request = new Request();
                    request.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REQUEST_ID)));
                    request.setRequesterId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REQUEST_RECIPIENT_ID)));
                    request.setPatientName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REQUEST_PATIENT_NAME)));
                    request.setHospital(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REQUEST_HOSPITAL)));
                    request.setBloodGroup(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REQUEST_BLOOD_GROUP)));
                    request.setUnits(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REQUEST_UNITS)));
                    request.setRequestDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REQUEST_DATE)));
                    request.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REQUEST_STATUS)));
                    requestList.add(request);
                } while (cursor.moveToNext());
            }
        }
        return requestList;
    }

    public List<Request> getAllBloodRequests() {
        List<Request> requestList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_REQUESTS, null, null, null, null, null, COLUMN_REQUEST_ID + " DESC")) {
            if (cursor.moveToFirst()) {
                do {
                    Request request = new Request();
                    request.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REQUEST_ID)));
                    request.setRequesterId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REQUEST_RECIPIENT_ID)));
                    request.setPatientName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REQUEST_PATIENT_NAME)));
                    request.setHospital(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REQUEST_HOSPITAL)));
                    request.setBloodGroup(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REQUEST_BLOOD_GROUP)));
                    request.setUnits(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REQUEST_UNITS)));
                    request.setRequestDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REQUEST_DATE)));
                    request.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REQUEST_STATUS)));
                    requestList.add(request);
                } while (cursor.moveToNext());
            }
        }
        return requestList;
    }

    public int updateRequestStatus(int requestId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_REQUEST_STATUS, newStatus);
        return db.update(TABLE_REQUESTS, values, COLUMN_REQUEST_ID + " = ?", new String[]{String.valueOf(requestId)});
    }

    public int countAllRequests() {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;
        try (Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_REQUESTS, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error counting all requests: " + e.getMessage());
        }
        return count;
    }

    public int countPendingRequests() {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;
        String query = "SELECT COUNT(*) FROM " + TABLE_REQUESTS + " WHERE " + COLUMN_REQUEST_STATUS + " = ?";
        try (Cursor cursor = db.rawQuery(query, new String[]{"Pending"})) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        }
        return count;
    }

    // =====================================================================
    // HÀM CHO BẢNG INVENTORY (KHO MÁU)
    // =====================================================================

    public long addBloodUnit(BloodUnit unit) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_INVENTORY_BLOOD_GROUP, unit.getBloodGroup());
        values.put(COLUMN_INVENTORY_UNITS, unit.getUnits());
        values.put(COLUMN_INVENTORY_EXPIRY_DATE, unit.getExpiryDate());
        values.put(COLUMN_INVENTORY_DONOR_ID, unit.getDonorId());
        return db.insert(TABLE_INVENTORY, null, values);
    }

    public int updateBloodUnit(BloodUnit unit) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_INVENTORY_BLOOD_GROUP, unit.getBloodGroup());
        values.put(COLUMN_INVENTORY_UNITS, unit.getUnits());
        values.put(COLUMN_INVENTORY_EXPIRY_DATE, unit.getExpiryDate());
        return db.update(TABLE_INVENTORY, values, COLUMN_INVENTORY_ID + " = ?", new String[]{String.valueOf(unit.getId())});
    }

    public void deleteBloodUnit(int inventoryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_INVENTORY, COLUMN_INVENTORY_ID + " = ?", new String[]{String.valueOf(inventoryId)});
    }

    public List<BloodUnit> getAllInventory() {
        List<BloodUnit> inventoryList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_INVENTORY, null)) {
            if (cursor.moveToFirst()) {
                do {
                    BloodUnit unit = new BloodUnit(
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_INVENTORY_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INVENTORY_BLOOD_GROUP)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_INVENTORY_UNITS)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INVENTORY_EXPIRY_DATE)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_INVENTORY_DONOR_ID))
                    );
                    inventoryList.add(unit);
                } while (cursor.moveToNext());
            }
        }
        return inventoryList;
    }

    public HashMap<String, Integer> getBloodGroupCounts() {
        HashMap<String, Integer> bloodGroupMap = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_INVENTORY_BLOOD_GROUP + ", SUM(" + COLUMN_INVENTORY_UNITS + ") as total_units FROM " + TABLE_INVENTORY + " GROUP BY " + COLUMN_INVENTORY_BLOOD_GROUP;
        try (Cursor cursor = db.rawQuery(query, null)) {
            if (cursor.moveToFirst()) {
                do {
                    int bloodGroupColIndex = cursor.getColumnIndexOrThrow(COLUMN_INVENTORY_BLOOD_GROUP);
                    String bloodGroup = cursor.getString(bloodGroupColIndex);
                    if (bloodGroup != null) {
                        int totalUnits = cursor.getInt(cursor.getColumnIndexOrThrow("total_units"));
                        bloodGroupMap.put(bloodGroup, totalUnits);
                    }
                } while (cursor.moveToNext());
            }
        }
        return bloodGroupMap;
    }
}
