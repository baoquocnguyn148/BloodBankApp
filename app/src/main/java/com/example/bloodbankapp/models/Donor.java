package com.example.bloodbankapp.models;

public class Donor {
    private int donorId;
    private int userId;
    private String lastDonationDate;
    private String name;
    private String bloodGroup;
    private String phone;
    private String address;

    // Constructors
    public Donor() {}

    public Donor(int userId, String lastDonationDate) {
        this.userId = userId;
        this.lastDonationDate = lastDonationDate;
    }

    // Getters and Setters
    public int getDonorId() { return donorId; }
    public void setDonorId(int donorId) { this.donorId = donorId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getLastDonationDate() { return lastDonationDate; }
    public void setLastDonationDate(String lastDonationDate) {
        this.lastDonationDate = lastDonationDate;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}