package com.example.bloodbankapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.bloodbankapp.models.BloodUnit;
import com.example.bloodbankapp.models.Request; // Đổi tên import nếu model của bạn là BloodRequest
import com.example.bloodbankapp.models.User;

import java.text.SimpleDateFormat; // Thêm import này
import java.util.ArrayList;
import java.util.Date;          // Thêm import này
import java.util.HashMap;
import java.util.List;
import java.util.Locale;        // Thêm import này
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bloodbank_unified.db";
    // Tăng phiên bản DB để kích hoạt onUpgrade khi cần
    private static final int DATABASE_VERSION = 2; // Giữ version 2 để không mất dữ liệu

    private static final String TAG = "DatabaseHelper"; // Thêm TAG để Log

    // --- Bảng USERS ---
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PASSWORD = "password"; // Giữ lại nếu bạn vẫn dùng (dù Firebase Auth xử lý)
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_PHONE = "phone";
    public static final String COLUMN_USER_ADDRESS = "address";
    public static final String COLUMN_USER_BLOOD_GROUP = "blood_group";
    public static final String COLUMN_USER_ROLE = "role";
    public static final String COLUMN_USER_FCM_TOKEN = "fcm_token"; // Đã có
    // *** THÊM CỘT MỚI VÍ DỤ ***
    public static final String COLUMN_USER_LAST_LOGIN = "last_login"; // Ví dụ cho onUpgrade version 3


    // --- Bảng REQUESTS ---
    public static final String TABLE_REQUESTS = "requests";
    public static final String COLUMN_REQUEST_ID = "request_id";
    public static final String COLUMN_REQUEST_RECIPIENT_ID = "recipient_id"; // Đã có: id người yêu cầu (user_id của recipient)
    public static final String COLUMN_REQUEST_PATIENT_NAME = "patient_name";
    public static final String COLUMN_REQUEST_HOSPITAL = "hospital";
    public static final String COLUMN_REQUEST_BLOOD_GROUP = "request_blood_group";
    public static final String COLUMN_REQUEST_UNITS = "units";
    public static final String COLUMN_REQUEST_DATE = "request_date";
    public static final String COLUMN_REQUEST_STATUS = "status"; // Pending, Approved, Rejected

    // --- Bảng INVENTORY ---
    public static final String TABLE_INVENTORY = "blood_inventory";
    public static final String COLUMN_INVENTORY_ID = "inventory_id";
    public static final String COLUMN_INVENTORY_BLOOD_GROUP = "inventory_blood_group";
    public static final String COLUMN_INVENTORY_UNITS = "units";
    public static final String COLUMN_INVENTORY_EXPIRY_DATE = "expiry_date";
    public static final String COLUMN_INVENTORY_DONOR_ID = "donor_id"; // ID người hiến (nếu có)

    // --- Bảng DONATION APPOINTMENTS ---
    public static final String TABLE_DONATION_APPOINTMENTS = "donation_appointments";
    public static final String COLUMN_APPOINTMENT_ID = "appointment_id";
    public static final String COLUMN_APPOINTMENT_DONOR_ID = "donor_id";
    public static final String COLUMN_APPOINTMENT_DONOR_NAME = "donor_name";
    public static final String COLUMN_APPOINTMENT_BLOOD_GROUP = "blood_group";
    public static final String COLUMN_APPOINTMENT_CENTER = "donation_center";
    public static final String COLUMN_APPOINTMENT_DATE = "donation_date";
    public static final String COLUMN_APPOINTMENT_TIME = "time_slot";
    public static final String COLUMN_APPOINTMENT_STATUS = "status"; // Pending, Approved, Completed, Cancelled
    public static final String COLUMN_APPOINTMENT_CREATED_AT = "created_at";


    // --- Câu lệnh tạo bảng ---
    private static final String CREATE_TABLE_USERS_V3 = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USER_EMAIL + " TEXT UNIQUE NOT NULL,"
            + COLUMN_USER_PASSWORD + " TEXT," // Giữ lại nếu cần
            + COLUMN_USER_NAME + " TEXT,"
            + COLUMN_USER_PHONE + " TEXT,"
            + COLUMN_USER_ADDRESS + " TEXT,"
            + COLUMN_USER_BLOOD_GROUP + " TEXT,"
            + COLUMN_USER_ROLE + " TEXT,"
            + COLUMN_USER_FCM_TOKEN + " TEXT,"
            + COLUMN_USER_LAST_LOGIN + " TEXT" // Thêm cột mới ở version 3
            + ")";

    // Câu lệnh tạo bảng Requests (giữ nguyên nếu không đổi)
    private static final String CREATE_TABLE_REQUESTS = "CREATE TABLE " + TABLE_REQUESTS + "("
            + COLUMN_REQUEST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_REQUEST_RECIPIENT_ID + " INTEGER NOT NULL," // Khóa ngoại tới users.user_id
            + COLUMN_REQUEST_PATIENT_NAME + " TEXT NOT NULL,"
            + COLUMN_REQUEST_HOSPITAL + " TEXT,"
            + COLUMN_REQUEST_BLOOD_GROUP + " TEXT NOT NULL,"
            + COLUMN_REQUEST_UNITS + " INTEGER NOT NULL,"
            + COLUMN_REQUEST_DATE + " TEXT," // Nên dùng kiểu INTEGER lưu timestamp hoặc TEXT dạng ISO 8601 'YYYY-MM-DD HH:MM:SS'
            + COLUMN_REQUEST_STATUS + " TEXT NOT NULL DEFAULT 'Pending'" // Thêm DEFAULT
            // + ", FOREIGN KEY(" + COLUMN_REQUEST_RECIPIENT_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")" // Thêm ràng buộc khóa ngoại (tùy chọn)
            + ")";

    // Câu lệnh tạo bảng Inventory (giữ nguyên nếu không đổi)
    private static final String CREATE_TABLE_INVENTORY = "CREATE TABLE " + TABLE_INVENTORY + "("
            + COLUMN_INVENTORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_INVENTORY_BLOOD_GROUP + " TEXT NOT NULL,"
            + COLUMN_INVENTORY_UNITS + " INTEGER NOT NULL CHECK(" + COLUMN_INVENTORY_UNITS + " >= 0)," // Đảm bảo số lượng không âm
            + COLUMN_INVENTORY_EXPIRY_DATE + " TEXT," // Nên dùng kiểu INTEGER lưu timestamp hoặc TEXT dạng 'YYYY-MM-DD'
            + COLUMN_INVENTORY_DONOR_ID + " INTEGER" // Khóa ngoại tới users.user_id (tùy chọn)
            // + ", FOREIGN KEY(" + COLUMN_INVENTORY_DONOR_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")" // Thêm ràng buộc khóa ngoại (tùy chọn)
            + ")";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "Creating database tables for version " + DATABASE_VERSION);
        db.execSQL(CREATE_TABLE_USERS_V3); // Dùng lệnh tạo bảng mới nhất
        db.execSQL(CREATE_TABLE_REQUESTS);
        db.execSQL(CREATE_TABLE_INVENTORY);
        addInitialAdminAccount(db);
        Log.i(TAG, "Database tables created.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);

        // Nâng cấp từ version 2 lên 3: Thêm cột last_login vào bảng users
        if (oldVersion < 3) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_USER_LAST_LOGIN + " TEXT");
                Log.i(TAG, "Successfully added '" + COLUMN_USER_LAST_LOGIN + "' column to " + TABLE_USERS + " table for version 3.");
            } catch (Exception e) {
                Log.e(TAG, "Error adding column '" + COLUMN_USER_LAST_LOGIN + "' to " + TABLE_USERS, e);
                // Xử lý lỗi: Có thể xóa và tạo lại bảng nếu ALTER thất bại (dữ liệu mất!)
                // db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
                // db.execSQL(CREATE_TABLE_USERS_V3); // Tạo lại bảng với cấu trúc mới
            }
        }

        // Thêm các khối 'if (oldVersion < X)' khác cho các phiên bản tương lai
        // if (oldVersion < 4) {
        //     // Ví dụ: Thêm cột 'notes' vào bảng requests
        //     try {
        //        db.execSQL("ALTER TABLE " + TABLE_REQUESTS + " ADD COLUMN notes TEXT");
        //        Log.i(TAG, "Added 'notes' column to requests table for version 4.");
        //     } catch (Exception e) {
        //        Log.e(TAG, "Error adding column 'notes' to " + TABLE_REQUESTS, e);
        //     }
        // }
    }

    // Bật hỗ trợ khóa ngoại (nếu bạn dùng ràng buộc FOREIGN KEY)
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // db.setForeignKeyConstraintsEnabled(true); // Bỏ comment nếu dùng FOREIGN KEY
    }


    private void addInitialAdminAccount(SQLiteDatabase db) {
        ContentValues adminValues = new ContentValues();
        adminValues.put(COLUMN_USER_EMAIL, "admin@bloodbank.com");
        adminValues.put(COLUMN_USER_NAME, "System Admin");
        adminValues.put(COLUMN_USER_ROLE, "admin");
        adminValues.put(COLUMN_USER_PHONE, "0000000000");
        adminValues.put(COLUMN_USER_ADDRESS, "Blood Bank HQ");
        adminValues.put(COLUMN_USER_BLOOD_GROUP, "N/A");
        
        long result = db.insertWithOnConflict(TABLE_USERS, null, adminValues, SQLiteDatabase.CONFLICT_IGNORE);
        if (result != -1) {
            Log.i(TAG, "Initial admin account added or already exists.");
        } else {
            Log.e(TAG, "Failed to add initial admin account.");
        }
    }

    // =====================================================================
    // HÀM CHO BẢNG USERS (Hợp nhất từ UserDAO và DatabaseHelper cũ)
    // =====================================================================

    // ... (Các hàm addUser, getUserByEmail, getUserById, getAllUsers, getUsersByRole, getDonorsByBloodGroup giữ nguyên như trước) ...
    public long addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_PHONE, user.getPhone());
        values.put(COLUMN_USER_ADDRESS, user.getAddress());
        values.put(COLUMN_USER_BLOOD_GROUP, user.getBloodGroup());
        values.put(COLUMN_USER_ROLE, user.getRole());
        values.put(COLUMN_USER_FCM_TOKEN, "");

        long id = -1;
        try {
            id = db.insertWithOnConflict(TABLE_USERS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            if (id == -1) {
                Log.w(TAG, "addUser failed, email might already exist: " + user.getEmail());
            } else {
                Log.i(TAG, "User added successfully with ID: " + id);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error adding user: " + user.getEmail(), e);
        } finally {
            closeDb(db);
        }
        return id;
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_USERS, null, COLUMN_USER_EMAIL + " = ?", new String[]{email}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                user = cursorToUser(cursor);
            } else {
                Log.d(TAG, "User not found with email: " + email);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user by email: " + email, e);
        } finally {
            closeCursor(cursor);
            closeDb(db);
        }
        return user;
    }

    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_USERS, null, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                user = cursorToUser(cursor);
            } else {
                Log.d(TAG, "User not found with ID: " + userId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user by ID: " + userId, e);
        } finally {
            closeCursor(cursor);
            closeDb(db);
        }
        return user;
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " ORDER BY " + COLUMN_USER_NAME, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    User user = cursorToUser(cursor);
                    userList.add(user);
                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.e(TAG, "Error getting all users", e);
        } finally {
            closeCursor(cursor);
            closeDb(db);
        }
        return userList;
    }

    public List<User> getUsersByRole(String role) {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            // Case-insensitive search using LOWER()
            cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE LOWER(" + COLUMN_USER_ROLE + ") = LOWER(?) ORDER BY " + COLUMN_USER_NAME, new String[]{role});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    User user = cursorToUser(cursor);
                    userList.add(user);
                } while (cursor.moveToNext());
            }
            Log.d(TAG, "getUsersByRole('" + role + "') returned " + userList.size() + " users");
        } catch (Exception e) {
            Log.e(TAG, "Error getting users by role: " + role, e);
        } finally {
            closeCursor(cursor);
            closeDb(db);
        }
        return userList;
    }

    public List<User> getDonorsByBloodGroup(String bloodGroup) {
        List<User> donorList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String selection = COLUMN_USER_ROLE + " = ? AND " + COLUMN_USER_BLOOD_GROUP + " = ?";
        String[] selectionArgs = { "donor", bloodGroup };
        try {
            cursor = db.query(TABLE_USERS, null, selection, selectionArgs, null, null, COLUMN_USER_NAME);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    User user = cursorToUser(cursor);
                    donorList.add(user);
                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.e(TAG, "Error getting donors by blood group: " + bloodGroup, e);
        } finally {
            closeCursor(cursor);
            closeDb(db);
        }
        return donorList;
    }

    public int updateUser(int userId, String name, String phone, String address, String bloodGroup, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, name);
        values.put(COLUMN_USER_PHONE, phone);
        values.put(COLUMN_USER_ADDRESS, address);
        values.put(COLUMN_USER_BLOOD_GROUP, bloodGroup);
        if (role != null) {
            values.put(COLUMN_USER_ROLE, role);
        }

        int rowsAffected = 0;
        try {
            rowsAffected = db.update(TABLE_USERS, values, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
            if (rowsAffected > 0) {
                Log.i(TAG, "User updated successfully: ID " + userId);
            } else {
                Log.w(TAG, "User update failed: ID " + userId + " not found?");
            }
        } catch (Exception e){
            Log.e(TAG, "Error updating user: ID " + userId, e);
        } finally {
            closeDb(db);
        }
        return rowsAffected;
    }

    public int updateUserFcmToken(int userId, String fcmToken) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_FCM_TOKEN, fcmToken);
        int rowsAffected = 0;
        try {
            rowsAffected = db.update(TABLE_USERS, values, COLUMN_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)});

            if (rowsAffected > 0) {
                Log.d(TAG, "FCM Token updated successfully for userId: " + userId);
            } else {
                Log.w(TAG, "Failed to update FCM Token for userId: " + userId + ". User not found?");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating FCM Token", e);
        } finally {
            closeDb(db);
        }
        return rowsAffected;
    }

    public int deleteUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = 0;
        try {
            rowsAffected = db.delete(TABLE_USERS, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
            if (rowsAffected > 0) {
                Log.i(TAG, "User deleted successfully: ID " + userId);
            } else {
                Log.w(TAG, "User deletion failed: ID " + userId + " not found?");
            }
        } catch (Exception e){
            Log.e(TAG, "Error deleting user: ID " + userId, e);
        } finally {
            closeDb(db);
        }
        return rowsAffected;
    }

    public int countUsersByRole(String role) {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;
        Cursor cursor = null;
        try {
            // Case-insensitive search using LOWER()
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USERS + " WHERE LOWER(" + COLUMN_USER_ROLE + ") = LOWER(?)", new String[]{role});
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            Log.d(TAG, "countUsersByRole('" + role + "') = " + count);
        } catch (Exception e) {
            Log.e(TAG, "Error counting users by role: " + role, e);
        } finally {
            closeCursor(cursor);
            closeDb(db);
        }
        return count;
    }

    // =====================================================================
    // HÀM CHO BẢNG REQUESTS (Chuyển đổi từ BloodRequestDao)
    // =====================================================================

    // ... (Các hàm addBloodRequest, getAllBloodRequests, getRequestsByUserId, updateRequestStatus, countPendingRequests, countAllRequests giữ nguyên như trước) ...
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

        long result = -1;
        try {
            result = db.insert(TABLE_REQUESTS, null, values);
            if (result != -1) {
                Log.i(TAG, "Blood request added successfully for recipient ID: " + recipientId);
            } else {
                Log.e(TAG, "Failed to add blood request for recipient ID: " + recipientId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error adding blood request", e);
        } finally {
            closeDb(db);
        }
        return result != -1;
    }

    public List<Request> getAllBloodRequests() {
        List<Request> requestList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_REQUESTS, null, null, null, null, null, COLUMN_REQUEST_ID + " DESC");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Request request = cursorToRequest(cursor);
                    // Kiểm tra null phòng trường hợp cursorToRequest trả về null do lỗi
                    if (request != null) {
                        requestList.add(request);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.e(TAG, "Error getting all blood requests", e);
        } finally {
            closeCursor(cursor);
            closeDb(db);
        }
        return requestList;
    }

    public List<Request> getRequestsByUserId(int userId) {
        List<Request> requestList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_REQUESTS, null, COLUMN_REQUEST_RECIPIENT_ID + " = ?",
                    new String[]{String.valueOf(userId)}, null, null, COLUMN_REQUEST_ID + " DESC");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Request request = cursorToRequest(cursor);
                    // Kiểm tra null phòng trường hợp cursorToRequest trả về null do lỗi
                    if (request != null) {
                        requestList.add(request);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.e(TAG, "Error getting requests by user ID: " + userId, e);
        } finally {
            closeCursor(cursor);
            closeDb(db);
        }
        return requestList;
    }

    public int updateRequestStatus(int requestId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_REQUEST_STATUS, newStatus);
        int rowsAffected = 0;
        try {
            rowsAffected = db.update(TABLE_REQUESTS, values, COLUMN_REQUEST_ID + " = ?", new String[]{String.valueOf(requestId)});
            if (rowsAffected > 0) {
                Log.i(TAG, "Request status updated successfully: ID " + requestId + " to " + newStatus);
            } else {
                Log.w(TAG, "Request status update failed: ID " + requestId + " not found?");
            }
        } catch (Exception e){
            Log.e(TAG, "Error updating request status: ID " + requestId, e);
        } finally {
            closeDb(db);
        }
        return rowsAffected;
    }

    public int countPendingRequests() {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;
        Cursor cursor = null;
        String query = "SELECT COUNT(*) FROM " + TABLE_REQUESTS + " WHERE " + COLUMN_REQUEST_STATUS + " = ?";
        try {
            cursor = db.rawQuery(query, new String[]{"Pending"});
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error counting pending requests", e);
        } finally {
            closeCursor(cursor);
            closeDb(db);
        }
        return count;
    }

    public int countAllRequests() {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_REQUESTS, null);
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error counting all requests", e);
        } finally {
            closeCursor(cursor);
            closeDb(db);
        }
        return count;
    }


    // =====================================================================
    // HÀM CHO BẢNG INVENTORY (Hoàn thiện từ InventoryDAO và DatabaseHelper cũ)
    // =====================================================================

    // ... (Các hàm addBloodUnit, updateBloodUnit, deleteBloodUnit, getAllInventory, getBloodGroupCounts, getTotalUnitsInInventory giữ nguyên như trước) ...
    public long addBloodUnit(BloodUnit unit) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_INVENTORY_BLOOD_GROUP, unit.getBloodGroup());
        values.put(COLUMN_INVENTORY_UNITS, unit.getUnits());
        values.put(COLUMN_INVENTORY_EXPIRY_DATE, unit.getExpiryDate());
        values.put(COLUMN_INVENTORY_DONOR_ID, unit.getDonorId());

        long id = -1;
        try {
            id = db.insert(TABLE_INVENTORY, null, values);
            if (id != -1) {
                Log.i(TAG, "Blood unit added successfully: ID " + id);
            } else {
                Log.e(TAG, "Failed to add blood unit for group: " + unit.getBloodGroup());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error adding blood unit", e);
        } finally {
            closeDb(db);
        }
        return id;
    }

    public int updateBloodUnit(BloodUnit unit) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_INVENTORY_BLOOD_GROUP, unit.getBloodGroup());
        values.put(COLUMN_INVENTORY_UNITS, unit.getUnits());
        values.put(COLUMN_INVENTORY_EXPIRY_DATE, unit.getExpiryDate());

        int rowsAffected = 0;
        try {
            rowsAffected = db.update(TABLE_INVENTORY, values, COLUMN_INVENTORY_ID + " = ?", new String[]{String.valueOf(unit.getId())});
            if (rowsAffected > 0) {
                Log.i(TAG, "Blood unit updated successfully: ID " + unit.getId());
            } else {
                Log.w(TAG, "Blood unit update failed: ID " + unit.getId() + " not found?");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating blood unit: ID " + unit.getId(), e);
        } finally {
            closeDb(db);
        }
        return rowsAffected;
    }

    public int deleteBloodUnit(int inventoryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = 0;
        try {
            rowsAffected = db.delete(TABLE_INVENTORY, COLUMN_INVENTORY_ID + " = ?", new String[]{String.valueOf(inventoryId)});
            if (rowsAffected > 0) {
                Log.i(TAG, "Blood unit deleted successfully: ID " + inventoryId);
            } else {
                Log.w(TAG, "Blood unit deletion failed: ID " + inventoryId + " not found?");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error deleting blood unit: ID " + inventoryId, e);
        } finally {
            closeDb(db);
        }
        return rowsAffected;
    }

    public List<BloodUnit> getAllInventory() {
        List<BloodUnit> inventoryList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_INVENTORY + " ORDER BY " + COLUMN_INVENTORY_BLOOD_GROUP + ", " + COLUMN_INVENTORY_EXPIRY_DATE, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    BloodUnit unit = cursorToBloodUnit(cursor);
                    if (unit != null) { // Kiểm tra null
                        inventoryList.add(unit);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.e(TAG, "Error getting all inventory", e);
        } finally {
            closeCursor(cursor);
            closeDb(db);
        }
        return inventoryList;
    }

    public HashMap<String, Integer> getBloodGroupCounts() {
        HashMap<String, Integer> bloodGroupMap = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String query = "SELECT " + COLUMN_INVENTORY_BLOOD_GROUP + ", SUM(" + COLUMN_INVENTORY_UNITS + ") as total_units FROM " + TABLE_INVENTORY + " GROUP BY " + COLUMN_INVENTORY_BLOOD_GROUP;
        try {
            cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int bloodGroupColIndex = cursor.getColumnIndex(COLUMN_INVENTORY_BLOOD_GROUP);
                    int totalUnitsColIndex = cursor.getColumnIndex("total_units");

                    if (bloodGroupColIndex != -1 && totalUnitsColIndex != -1) {
                        String bloodGroup = cursor.getString(bloodGroupColIndex);
                        if (bloodGroup != null) {
                            int totalUnits = cursor.getInt(totalUnitsColIndex);
                            bloodGroupMap.put(bloodGroup, totalUnits);
                        } else {
                            Log.w(TAG, "Found null blood group in inventory count query.");
                        }
                    } else {
                        Log.e(TAG, "Column index not found in getBloodGroupCounts query.");
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.e(TAG, "Error calculating blood group counts", e);
        } finally {
            closeCursor(cursor);
            closeDb(db);
        }
        return bloodGroupMap;
    }

    public int getTotalUnitsInInventory() {
        SQLiteDatabase db = this.getReadableDatabase();
        int totalUnits = 0;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT SUM(" + COLUMN_INVENTORY_UNITS + ") FROM " + TABLE_INVENTORY, null);
            if (cursor != null && cursor.moveToFirst()) {
                totalUnits = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error calculating total units in inventory", e);
        } finally {
            closeCursor(cursor);
            closeDb(db);
        }
        return totalUnits;
    }


    // =====================================================================
    // HÀM TIỆN ÍCH (HELPER METHODS)
    // =====================================================================

    // ... (Hàm cursorToUser giữ nguyên) ...
    private User cursorToUser(Cursor cursor) {
        try {
            return new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PHONE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ADDRESS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_BLOOD_GROUP)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ROLE))
            );
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Error converting cursor to User: Column not found", e);
            return null;
        }
    }


    /**
     * Chuyển đổi dữ liệu từ Cursor sang đối tượng Request.
     * Lưu ý: Hàm này chủ yếu để tương thích ngược với code SQLite cũ và sẽ không cần thiết
     * khi hoàn toàn chuyển sang Firestore để đọc dữ liệu Request.
     * @param cursor Cursor đang trỏ đến một hàng trong bảng requests.
     * @return Đối tượng Request.
     */
    private Request cursorToRequest(Cursor cursor) {
        // ✅ ĐÃ SỬA LỖI TRONG PHIÊN BẢN NÀY
        try {
            Request request = new Request();
            request.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REQUEST_ID)));

            // Lấy ID người dùng (int) từ DB và chuyển thành String.
            int recipientIdInt = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REQUEST_RECIPIENT_ID));
            request.setRequesterUid(String.valueOf(recipientIdInt)); // Chuyển int thành String

            request.setPatientName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REQUEST_PATIENT_NAME)));
            request.setHospital(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REQUEST_HOSPITAL)));
            request.setBloodGroup(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REQUEST_BLOOD_GROUP)));
            request.setUnits(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REQUEST_UNITS)));

            // Đọc chuỗi ngày tháng từ DB và cố gắng parse thành đối tượng Date.
            String dateString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REQUEST_DATE));
            try {
                // Giả sử định dạng trong DB là "yyyy-MM-dd HH:mm:ss"
                SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date parsedDate = dbDateFormat.parse(dateString);
                request.setRequestTimestamp(parsedDate); // Gán đối tượng Date
            } catch (Exception e) {
                Log.w(TAG, "Could not parse date string from SQLite: " + dateString + ". Setting timestamp to null.", e);
                request.setRequestTimestamp(null); // Gán null nếu parse lỗi
            }

            request.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REQUEST_STATUS)));
            // Không set documentId vì đây là dữ liệu từ SQLite
            return request;
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Error converting cursor to Request: Column not found", e);
            return null;
        } catch (Exception e) { // Bắt thêm các lỗi khác như ParseException
            Log.e(TAG, "Error converting cursor to Request", e);
            return null;
        }
    }


    // ... (Hàm cursorToBloodUnit giữ nguyên) ...
    private BloodUnit cursorToBloodUnit(Cursor cursor) {
        try {
            return new BloodUnit(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_INVENTORY_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INVENTORY_BLOOD_GROUP)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_INVENTORY_UNITS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INVENTORY_EXPIRY_DATE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_INVENTORY_DONOR_ID))
            );
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Error converting cursor to BloodUnit: Column not found", e);
            return null;
        }
    }


    // ... (Hàm closeCursor giữ nguyên) ...
    private void closeCursor(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }


    // ... (Hàm closeDb giữ nguyên) ...
    private void closeDb(SQLiteDatabase db) {
        // Tạm thời không đóng để SQLiteOpenHelper quản lý
    }
}