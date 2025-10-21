package com.example.bloodbankapp.models;

public class User {

    private int userId;
    private String email;
    private String name;
    private String phone;
    private String address;
    private String bloodGroup;
    private String role;


    public User() {
    }


    public User(int userId, String email, String name, String phone, String address, String bloodGroup, String role) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.bloodGroup = bloodGroup;
        this.role = role;
    }


    public User(String email, String name, String phone, String address, String bloodGroup, String role) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.bloodGroup = bloodGroup;
        this.role = role;
    }


    public int getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public String getRole() {
        return role;
    }

    // --- SETTERS ---

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
