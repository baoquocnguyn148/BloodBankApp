package com.example.bloodbankapp.database;

import android.content.Context;

import com.example.bloodbankapp.models.BloodUnit;
// ✅ SỬA LỖI: TÁCH CÁC IMPORT RA RIÊNG DÒNG
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * LỚP DAO DÀNH CHO KHO MÁU (INVENTORY)
 *
 * LƯU Ý QUAN TRỌNG:
 * Cấu trúc database hiện tại (trong UserDAO.java) KHÔNG CÓ bảng 'inventory'.
 * Do đó, tất cả các phương thức trong file này sẽ chỉ trả về giá trị rỗng hoặc mặc định
 * để ứng dụng không bị crash.
 *
 * Để các chức năng này hoạt động, bạn sẽ cần phải hợp nhất cấu trúc database,
 * tạo bảng 'inventory' bên trong file quản lý DB chính.
 */
public class InventoryDAO {

    public InventoryDAO(Context context) {
        // Constructor này không cần làm gì cả vì không có bảng inventory để kết nối.
    }

    /**
     * [KHÔNG HOẠT ĐỘNG] - Trả về -1 vì không có bảng để thêm.
     */
    public long addBloodUnit(BloodUnit unit) {
        return -1; // Không thể thêm
    }

    /**
     * [KHÔNG HOẠT ĐỘNG] - Trả về 0 vì không có gì để cập nhật.
     */
    public int updateBloodUnit(BloodUnit unit) {
        return 0; // Không có hàng nào được cập nhật
    }

    /**
     * [KHÔNG HOẠT ĐỘNG] - Không làm gì cả.
     */
    public void deleteBloodUnit(int inventoryId) {
        // Không thể xóa
    }

    /**
     * [KHÔNG HOẠT ĐỘNG] - Luôn trả về một danh sách rỗng.
     */
    public List<BloodUnit> getAllInventory() {
        return new ArrayList<>(); // Luôn trả về danh sách rỗng
    }

    /**
     * [KHÔNG HOẠT ĐỘNG] - Luôn trả về 0.
     */
    public int getTotalUnits() {
        return 0; // Tổng số lượng là 0
    }

    /**
     * [KHÔNG HOẠT ĐỘNG] - Luôn trả về một HashMap rỗng.
     */
    public HashMap<String, Integer> getAllBloodGroups() {
        return new HashMap<>(); // Luôn trả về map rỗng
    }
}
