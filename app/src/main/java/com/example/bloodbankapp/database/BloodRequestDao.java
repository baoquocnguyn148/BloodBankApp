package com.example.bloodbankapp.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.bloodbankapp.models.BloodRequest;

import java.util.List;


@Dao
public interface BloodRequestDao {


    @Query("SELECT * FROM blood_requests ORDER BY id DESC")
    List<BloodRequest> getAllRequests();


    @Query("SELECT * FROM blood_requests WHERE recipientId = :userId ORDER BY id DESC")
    List<BloodRequest> getRequestsByUserId(int userId);

    @Insert
    void insert(BloodRequest bloodRequest);

   @Query("UPDATE blood_requests SET status = :newStatus WHERE id = :requestId")
    void updateStatus(int requestId, String newStatus);

    @Query("SELECT COUNT(*) FROM blood_requests WHERE status = 'Pending'")
    int countPendingRequests();
}
