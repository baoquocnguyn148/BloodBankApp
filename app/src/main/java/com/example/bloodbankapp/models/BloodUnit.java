package com.example.bloodbankapp.models;

public class BloodUnit {
    private int id;
    private String bloodGroup;
    private int units;
    private String expiryDate;
    private int donorId;

    // Constructor mặc định
    public BloodUnit() {
    }

    public BloodUnit(int id, String bloodGroup, int units, String expiryDate, int donorId) {
        this.id = id;
        this.bloodGroup = bloodGroup;
        this.units = units;
        this.expiryDate = expiryDate;
        this.donorId = donorId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }


    public int getDonorId() {
        return donorId;
    }

    public void setDonorId(int donorId) {
        this.donorId = donorId;
    }


}
