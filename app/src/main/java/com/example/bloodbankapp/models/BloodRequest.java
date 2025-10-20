package com.example.bloodbankapp.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "blood_requests")
public class BloodRequest {

    @PrimaryKey(autoGenerate = true)
    private int id;

    // --- Các trường mới được thêm vào để hiển thị đầy đủ thông tin cho Admin ---
    private String patientName;
    private String hospital;
    private String requestDate;
    // -----------------------------------------------------------------------

    private int recipientId; // ID của người nhận (giữ lại để tham chiếu nếu cần)
    private String bloodGroup; // Nhóm máu cần
    private int unitsNeeded; // Số lượng đơn vị cần
    private String status; // Trạng thái: Pending, Approved, Rejected

    // Constructors
    public BloodRequest(String patientName, String hospital, String requestDate, int recipientId, String bloodGroup, int unitsNeeded, String status) {
        this.patientName = patientName;
        this.hospital = hospital;
        this.requestDate = requestDate;
        this.recipientId = recipientId;
        this.bloodGroup = bloodGroup;
        this.unitsNeeded = unitsNeeded;
        this.status = status;
    }

    // Getters
    public int getId() { return id; }
    public String getPatientName() { return patientName; }
    public String getHospital() { return hospital; }
    public String getRequestDate() { return requestDate; }
    public int getRecipientId() { return recipientId; }
    public String getBloodGroup() { return bloodGroup; }
    public int getUnitsNeeded() { return unitsNeeded; }
    public String getStatus() { return status; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public void setHospital(String hospital) { this.hospital = hospital; }
    public void setRequestDate(String requestDate) { this.requestDate = requestDate; }
    public void setRecipientId(int recipientId) { this.recipientId = recipientId; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
    public void setUnitsNeeded(int unitsNeeded) { this.unitsNeeded = unitsNeeded; }
    public void setStatus(String status) { this.status = status; }
}
