package com.example.bloodbankapp.models;

import androidx.annotation.NonNull;
import java.util.Objects;

/**
 * Lớp Model (POJO) đại diện cho thông tin tồn kho của một nhóm máu.
 * Nó lưu trữ nhóm máu và số lượng đơn vị máu tương ứng.
 */
public class BloodStock {

    private String bloodGroup;
    private int units;

    /**
     * Constructor rỗng.
     * Bắt buộc phải có cho các thư viện như Firebase hoặc các công cụ ánh xạ đối tượng khác.
     */
    public BloodStock() {
    }

    /**
     * Constructor đầy đủ để tạo một đối tượng BloodStock mới một cách nhanh chóng.
     *
     * @param bloodGroup Nhóm máu (ví dụ: "A+", "O-").
     * @param units      Số lượng đơn vị máu có sẵn.
     */
    public BloodStock(String bloodGroup, int units) {
        this.bloodGroup = bloodGroup;
        this.units = units;
    }

    // --- GETTERS AND SETTERS ---

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


    // --- UTILITY METHODS ---

    /**
     * Trả về một chuỗi đại diện cho đối tượng, rất hữu ích cho việc gỡ lỗi (debugging).
     * Ví dụ: "BloodStock{bloodGroup='A+', units=100}"
     */
    @NonNull
    @Override
    public String toString() {
        return "BloodStock{" +
                "bloodGroup='" + bloodGroup + '\'' +
                ", units=" + units +
                '}';
    }

    /**
     * So sánh hai đối tượng BloodStock với nhau.
     * Hai đối tượng được coi là bằng nhau nếu chúng có cùng nhóm máu và số lượng.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BloodStock that = (BloodStock) o;
        return units == that.units && Objects.equals(bloodGroup, that.bloodGroup);
    }

    /**
     * Tạo một mã hash duy nhất cho đối tượng, cần thiết khi sử dụng trong các cấu trúc dữ liệu như HashMap.
     */
    @Override
    public int hashCode() {
        return Objects.hash(bloodGroup, units);
    }
}
