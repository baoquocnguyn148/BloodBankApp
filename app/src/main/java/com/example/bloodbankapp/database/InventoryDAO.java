package com.example.bloodbankapp.database;

import android.content.Context;

import com.example.bloodbankapp.models.BloodUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InventoryDAO {

    public InventoryDAO(Context context) {
        // Constructor này không cần làm gì cả vì không có bảng inventory để kết nối.
    }


    public long addBloodUnit(BloodUnit unit) {
        return -1; // Không thể thêm
    }


    public int updateBloodUnit(BloodUnit unit) {
        return 0; // Không có hàng nào được cập nhật
    }


    public void deleteBloodUnit(int inventoryId) {
        // Không thể xóa
    }


    public List<BloodUnit> getAllInventory() {
        return new ArrayList<>(); // Luôn trả về danh sách rỗng
    }


    public int getTotalUnits() {
        return 0; // Tổng số lượng là 0
    }


    public HashMap<String, Integer> getAllBloodGroups() {
        return new HashMap<>(); // Luôn trả về map rỗng
    }
}
