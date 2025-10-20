package com.example.bloodbankapp.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloodbankapp.R;
import com.example.bloodbankapp.adapters.BloodUnitAdapter;
import com.example.bloodbankapp.database.DatabaseHelper;
import com.example.bloodbankapp.models.BloodUnit;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class ManageInventoryActivity extends AppCompatActivity implements BloodUnitAdapter.OnDataChangedListener {

    private RecyclerView rvInventory;
    private BloodUnitAdapter adapter;
    private List<BloodUnit> bloodUnitList = new ArrayList<>();
    private FloatingActionButton fabAddBloodUnit;
    private Toolbar toolbar;
    private DatabaseHelper dbHelper;
    private TextView tvEmptyInventory; // Thêm TextView để báo rỗng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_inventory);

        dbHelper = new DatabaseHelper(this);
        initViews();
        setupToolbar();
        setupRecyclerView();
        setupClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadInventoryData(); // Luôn tải lại dữ liệu khi quay lại màn hình
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_inventory);
        rvInventory = findViewById(R.id.rv_inventory);
        fabAddBloodUnit = findViewById(R.id.fab_add_blood_unit);
        tvEmptyInventory = findViewById(R.id.tv_empty_inventory); // Ánh xạ TextView báo rỗng
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Manage Inventory");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        adapter = new BloodUnitAdapter(this, bloodUnitList, this);
        rvInventory.setLayoutManager(new LinearLayoutManager(this));
        rvInventory.setAdapter(adapter);
    }

    private void setupClickListeners() {
        fabAddBloodUnit.setOnClickListener(v -> {
            showAddBloodUnitDialog(); // Mở dialog để thêm mới
        });
    }

    private void loadInventoryData() {
        List<BloodUnit> latestData = dbHelper.getAllInventory();
        bloodUnitList.clear();
        bloodUnitList.addAll(latestData);
        adapter.notifyDataSetChanged();

        // Hiển thị hoặc ẩn thông báo danh sách rỗng
        if (bloodUnitList.isEmpty()) {
            rvInventory.setVisibility(View.GONE);
            tvEmptyInventory.setVisibility(View.VISIBLE);
        } else {
            rvInventory.setVisibility(View.VISIBLE);
            tvEmptyInventory.setVisibility(View.GONE);
        }
    }

    private void showAddBloodUnitDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Add New Blood Unit");

        // Inflate layout cho dialog
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_blood_unit, null);
        builder.setView(dialogView);

        final AutoCompleteTextView etBloodGroup = dialogView.findViewById(R.id.et_dialog_blood_group);
        final TextInputEditText etUnits = dialogView.findViewById(R.id.et_dialog_units);
        final TextInputEditText etExpiryDate = dialogView.findViewById(R.id.et_dialog_expiry_date);

        // Setup ArrayAdapter cho Spinner nhóm máu
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.blood_groups, android.R.layout.simple_spinner_dropdown_item);
        etBloodGroup.setAdapter(adapter);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String bloodGroup = etBloodGroup.getText().toString().trim();
            String unitsStr = etUnits.getText().toString().trim();
            String expiryDate = etExpiryDate.getText().toString().trim();

            if (TextUtils.isEmpty(bloodGroup) || TextUtils.isEmpty(unitsStr) || TextUtils.isEmpty(expiryDate)) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            int units = Integer.parseInt(unitsStr);
            // Giả sử donorId là 1 cho đơn giản, bạn có thể thay đổi logic này
            BloodUnit newUnit = new BloodUnit(0, bloodGroup, units, expiryDate, 1);

            long result = dbHelper.addBloodUnit(newUnit);
            if (result != -1) {
                Toast.makeText(this, "Blood unit added successfully!", Toast.LENGTH_SHORT).show();
                loadInventoryData(); // Tải lại dữ liệu để hiển thị đơn vị mới
            } else {
                Toast.makeText(this, "Failed to add blood unit.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    @Override
    public void onDataChanged() {
        // Hàm này được gọi từ Adapter sau khi xóa thành công
        loadInventoryData();
    }

    // Bạn cần tạo thêm một file layout `dialog_add_blood_unit.xml` cho dialog
    // Và một file array `blood_groups` trong `res/values/strings.xml`
}
