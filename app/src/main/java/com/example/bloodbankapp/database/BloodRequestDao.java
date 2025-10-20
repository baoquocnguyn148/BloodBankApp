package com.example.bloodbankapp.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.bloodbankapp.models.BloodRequest;

import java.util.List;

// ✅ SỬA LỖI: Đã thêm khoảng trắng giữa "@Dao" và "public"
@Dao
public interface BloodRequestDao {

    // Lấy tất cả các yêu cầu, sắp xếp theo ID mới nhất lên đầu
    @Query("SELECT * FROM blood_requests ORDER BY id DESC")
    List<BloodRequest> getAllRequests();

    // Lấy các yêu cầu của một người dùng cụ thể
    @Query("SELECT * FROM blood_requests WHERE recipientId = :userId ORDER BY id DESC")
    List<BloodRequest> getRequestsByUserId(int userId);

    // Thêm một yêu cầu mới
    @Insert
    void insert(BloodRequest bloodRequest);

    // Cập nhật trạng thái của một yêu cầu
    @Query("UPDATE blood_requests SET status = :newStatus WHERE id = :requestId")
    void updateStatus(int requestId, String newStatus);

    // Đếm các yêu cầu đang chờ
    @Query("SELECT COUNT(*) FROM blood_requests WHERE status = 'Pending'")
    int countPendingRequests();
}
