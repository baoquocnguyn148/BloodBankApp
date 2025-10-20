package com.example.bloodbankapp.activities; // Hoặc com.example.bloodbankapp.model

public class BloodRequest {
    private String patientName;
    private String hospitalName;
    private String bloodType;
    private String requestDate;

    // Constructor rỗng (quan trọng cho Firebase sau này)
    public BloodRequest() {}

    // Constructor đầy đủ để tạo đối tượng
    public BloodRequest(String patientName, String hospitalName, String bloodType, String requestDate) {
        this.patientName = patientName;
        this.hospitalName = hospitalName;
        this.bloodType = bloodType;
        this.requestDate = requestDate;
    }

    // Các hàm Getters để lấy dữ liệu ra
    public String getPatientName() { return patientName; }
    public String getHospitalName() { return hospitalName; }
    public String getBloodType() { return bloodType; }
    public String getRequestDate() { return requestDate; }
}
