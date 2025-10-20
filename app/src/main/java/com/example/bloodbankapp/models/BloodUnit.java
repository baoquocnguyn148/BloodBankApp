package com.example.bloodbankapp.models;

public class BloodUnit {
    private int id;
    private String bloodGroup;
    private int units; // ✅ SỬA 1: THÊM TRƯỜNG "UNITS" (SỐ LƯỢNG)
    private String expiryDate;
    private int donorId; // ✅ SỬA 2: ĐỔI "DONORID" TỪ STRING SANG INT

    // Constructor mặc định
    public BloodUnit() {
    }

    // ✅ SỬA 3: CẬP NHẬT LẠI CONSTRUCTOR ĐẦY ĐỦ
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

    // ✅ SỬA 4: THÊM GETTER VÀ SETTER CHO "UNITS"
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

    // ✅ SỬA 5: SỬA LẠI GETTER VÀ SETTER CHO "DONORID"
    public int getDonorId() {
        return donorId;
    }

    public void setDonorId(int donorId) {
        this.donorId = donorId;
    }

    // Ghi chú: Trường "status" không có trong bảng "inventory" của DatabaseHelper,
    // nên chúng ta có thể tạm thời bỏ nó đi để tránh nhầm lẫn.
}
