package com.example.bloodbankapp.models;

import com.google.firebase.firestore.IgnoreExtraProperties; // ✅ Thêm import này

@IgnoreExtraProperties // Giúp Firestore bỏ qua các trường không khớp
public class User {

    private String uid; // ✅ Thêm trường uid (ID từ Firebase Auth)
    private int userId; // ID cũ từ SQLite (có thể giữ lại hoặc bỏ)
    private String email;
    private String name;
    private String phone;
    private String address;
    private String bloodGroup;
    private String role;

    // Constructor rỗng (BẮT BUỘC cho Firestore)
    public User() {
    }

    // ✅ CONSTRUCTOR MỚI DÙNG CHO FIRESTORE (nhận String uid)
    public User(String uid, String email, String name, String phone, String address, String bloodGroup, String role) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.bloodGroup = bloodGroup;
        this.role = role;
        // this.userId = 0; // Gán giá trị mặc định nếu vẫn cần userId (int)
    }


    // Constructor cũ (giữ lại nếu cần cho tương thích ngược)
    public User(int userId, String email, String name, String phone, String address, String bloodGroup, String role) {
        // uid có thể để null hoặc "" ban đầu khi dùng constructor này
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.bloodGroup = bloodGroup;
        this.role = role;
    }

    // Constructor không có ID (giữ lại nếu cần)
    public User(String email, String name, String phone, String address, String bloodGroup, String role) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.bloodGroup = bloodGroup;
        this.role = role;
    }


    // --- GETTERS ---
    public String getUid() { return uid; } // ✅ Getter cho uid
    public int getUserId() { return userId; } // Giữ lại nếu cần
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getBloodGroup() { return bloodGroup; }
    public String getRole() { return role; }

    // --- SETTERS ---
    public void setUid(String uid) { this.uid = uid; } // ✅ Setter cho uid
    public void setUserId(int userId) { this.userId = userId; } // Giữ lại nếu cần
    public void setEmail(String email) { this.email = email; }
    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
    public void setRole(String role) { this.role = role; }
}