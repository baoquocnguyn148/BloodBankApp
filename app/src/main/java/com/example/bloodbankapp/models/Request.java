package com.example.bloodbankapp.models;

import com.google.firebase.firestore.Exclude; // ✅ Import Exclude
import com.google.firebase.firestore.IgnoreExtraProperties; // ✅ Import IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp; // ✅ Import ServerTimestamp (nếu dùng Date)
import java.util.Date; // ✅ Import Date (nếu dùng Date)

@IgnoreExtraProperties // Giúp Firestore bỏ qua các trường không khớp
public class Request {

    // Document ID từ Firestore, dùng @Exclude để không lưu trường này vào Firestore
    @Exclude
    private String documentId;

    // Các trường dữ liệu sẽ lưu vào Firestore
    private int id; // Có thể không cần thiết nữa nếu dùng documentId
    // private int requesterId; // Nên thay bằng requesterUid (String)
    private String requesterUid; // ✅ ID của người dùng Firebase Auth yêu cầu
    private String hospital;
    private String bloodGroup;
    // private String requestDate; // Nên dùng Timestamp
    @ServerTimestamp // ✅ Firestore tự động điền timestamp khi tạo
    private Date requestTimestamp; // ✅ Lưu dưới dạng Timestamp
    private String status;
    private String patientName;
    private int units;

    // Constructor rỗng (BẮT BUỘC cho Firestore)
    public Request() {
    }

    // --- GETTERS ---

    @Exclude // Không lấy trường này từ Firestore
    public String getDocumentId() {
        return documentId;
    }


    public int getId() { return id; }
    public String getRequesterUid() { return requesterUid; }
    public String getHospital() { return hospital; }
    public String getBloodGroup() { return bloodGroup; }
    public Date getRequestTimestamp() { return requestTimestamp; }
    public String getStatus() { return status; }
    public String getPatientName() { return patientName; }
    public int getUnits() { return units; }

    // Phương thức getQuantity() là bí danh (alias) của getUnits()
    public int getQuantity() {
        return getUnits();
    }

    // --- SETTERS ---

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    // Các setter khác
    public void setId(int id) { this.id = id; }
    public void setRequesterUid(String requesterUid) { this.requesterUid = requesterUid; } // ✅ Setter cho requesterUid
    public void setHospital(String hospital) { this.hospital = hospital; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
    public void setRequestTimestamp(Date requestTimestamp) { this.requestTimestamp = requestTimestamp; } // ✅ Setter cho Timestamp
    public void setStatus(String status) { this.status = status; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public void setUnits(int units) { this.units = units; }
}