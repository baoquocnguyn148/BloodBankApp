package com.example.bloodbankapp.models;

public class Request {
    private int id;
    private int requesterId;
    private String hospital;
    private String bloodGroup;
    private String requestDate;
    private String status;
    private String patientName;
    private int units; // Tên trường dữ liệu là 'units'

    // Constructor rỗng
    public Request() {
    }

    // --- GETTERS VÀ SETTERS ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(int requesterId) {
        this.requesterId = requesterId;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public int getUnits() {
        return units;
    }

    // Phương thức getQuantity() là bí danh (alias) của getUnits()
    // để đảm bảo code cũ và mới đều hoạt động.
    public int getQuantity() {
        return getUnits();
    }

    public void setUnits(int units) {
        this.units = units;
    }
}
