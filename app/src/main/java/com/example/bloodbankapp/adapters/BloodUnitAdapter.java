package com.example.bloodbankapp.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloodbankapp.R;
import com.example.bloodbankapp.database.DatabaseHelper;
import com.example.bloodbankapp.models.BloodUnit;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;


public class BloodUnitAdapter extends RecyclerView.Adapter<BloodUnitAdapter.BloodUnitViewHolder> {

    private final Context context;
    private final List<BloodUnit> bloodUnitList;
    private final DatabaseHelper dbHelper;

    public interface OnDataChangedListener {
        void onDataChanged();
    }

    private final OnDataChangedListener onDataChangedListener;

    // Constructor đã đúng tên
    public BloodUnitAdapter(Context context, List<BloodUnit> bloodUnitList, OnDataChangedListener listener) {
        this.context = context;
        this.bloodUnitList = bloodUnitList;
        this.dbHelper = new DatabaseHelper(context);
        this.onDataChangedListener = listener;
    }

    @NonNull
    @Override
    public BloodUnitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_blood_unit, parent, false);
        return new BloodUnitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BloodUnitViewHolder holder, int position) {
        BloodUnit unit = bloodUnitList.get(position);

        holder.tvBloodGroup.setText("Blood Group: " + unit.getBloodGroup());
        holder.tvUnits.setText("Units: " + unit.getUnits());
        holder.tvExpiryDate.setText("Expires: " + unit.getExpiryDate());

        // ✅ SỬA LỖI TẠI ĐÂY: Gán sự kiện cho nút Sửa
        holder.btnEdit.setOnClickListener(v -> {
            // Gọi hàm hiển thị dialog sửa thông tin
            showEditDialog(unit);
        });

        // Giữ nguyên sự kiện cho nút Xóa, nó đã hoạt động đúng
        holder.btnDelete.setOnClickListener(v -> {
            showDeleteConfirmationDialog(unit);
        });
    }

    /**
     * ✅ HÀM MỚI: Hiển thị dialog để SỬA thông tin BloodUnit.
     */
    private void showEditDialog(BloodUnit unitToEdit) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Edit Blood Unit");

        // Tái sử dụng layout dialog của chức năng "Add"
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_blood_unit, null);
        builder.setView(dialogView);

        final AutoCompleteTextView etBloodGroup = dialogView.findViewById(R.id.et_dialog_blood_group);
        final TextInputEditText etUnits = dialogView.findViewById(R.id.et_dialog_units);
        final TextInputEditText etExpiryDate = dialogView.findViewById(R.id.et_dialog_expiry_date);

        // Setup ArrayAdapter cho Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.blood_groups, android.R.layout.simple_spinner_dropdown_item);
        etBloodGroup.setAdapter(adapter);

        // --- Điền dữ liệu cũ của 'unitToEdit' vào các trường trong dialog ---
        etBloodGroup.setText(unitToEdit.getBloodGroup(), false); // 'false' để không lọc lại danh sách
        etUnits.setText(String.valueOf(unitToEdit.getUnits()));
        etExpiryDate.setText(unitToEdit.getExpiryDate());
        // ----------------------------------------------------------------

        builder.setPositiveButton("Save", (dialog, which) -> {
            String bloodGroup = etBloodGroup.getText().toString().trim();
            String unitsStr = etUnits.getText().toString().trim();
            String expiryDate = etExpiryDate.getText().toString().trim();

            if (TextUtils.isEmpty(bloodGroup) || TextUtils.isEmpty(unitsStr) || TextUtils.isEmpty(expiryDate)) {
                Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            // Cập nhật thông tin vào đối tượng 'unitToEdit'
            unitToEdit.setBloodGroup(bloodGroup);
            unitToEdit.setUnits(Integer.parseInt(unitsStr));
            unitToEdit.setExpiryDate(expiryDate);

            // Gọi hàm update trong DatabaseHelper
            int rowsAffected = dbHelper.updateBloodUnit(unitToEdit);
            if (rowsAffected > 0) {
                Toast.makeText(context, "Blood unit updated successfully!", Toast.LENGTH_SHORT).show();
                if (onDataChangedListener != null) {
                    // Báo cho Activity tải lại dữ liệu để giao diện được cập nhật
                    onDataChangedListener.onDataChanged();
                }
            } else {
                Toast.makeText(context, "Failed to update blood unit.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    // Hàm xóa này đã đúng, giữ nguyên
    private void showDeleteConfirmationDialog(BloodUnit unit) {
        new MaterialAlertDialogBuilder(context)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete this blood unit (ID: " + unit.getId() + ")?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteBloodUnit(unit.getId());
                    Toast.makeText(context, "Blood unit deleted.", Toast.LENGTH_SHORT).show();
                    if (onDataChangedListener != null) {
                        onDataChangedListener.onDataChanged();
                    }
                })
                .show();
    }

    @Override
    public int getItemCount() {
        return bloodUnitList.size();
    }

    // ViewHolder đã đúng tên
    public static class BloodUnitViewHolder extends RecyclerView.ViewHolder {
        TextView tvBloodGroup, tvUnits, tvExpiryDate;
        Button btnEdit, btnDelete;

        public BloodUnitViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBloodGroup = itemView.findViewById(R.id.tv_blood_group);
            tvUnits = itemView.findViewById(R.id.tv_units);
            tvExpiryDate = itemView.findViewById(R.id.tv_expiry_date);
            btnEdit = itemView.findViewById(R.id.btn_edit_unit);
            btnDelete = itemView.findViewById(R.id.btn_delete_unit);
        }
    }
}
