package com.example.bloodbankapp.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.bloodbankapp.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ScheduleDonationActivity extends AppCompatActivity {

    private AutoCompleteTextView actvCenter;
    private TextInputEditText etDate;
    private ChipGroup chipGroupTime;
    private Button btnConfirmSchedule;
    private Toolbar toolbar;

    private String selectedTimeSlot = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_donation);

        // Ánh xạ views
        initViews();

        // Thiết lập Toolbar
        setupToolbar();

        // Thiết lập các chức năng
        setupDonationCenters();
        setupDatePicker();
        setupTimeSlotSelection();
        setupConfirmButton();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_schedule);
        actvCenter = findViewById(R.id.actv_center);
        etDate = findViewById(R.id.et_date);
        chipGroupTime = findViewById(R.id.chip_group_time);
        btnConfirmSchedule = findViewById(R.id.btn_confirm_schedule);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        // Bật nút back
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // Bắt sự kiện khi nhấn nút back
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupDonationCenters() {
        // Dữ liệu giả, sau này bạn có thể lấy từ database hoặc API
        String[] centers = new String[]{"Central Blood Bank", "City General Hospital", "District 5 Donation Center"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, centers);
        actvCenter.setAdapter(adapter);
    }

    private void setupDatePicker() {
        etDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    ScheduleDonationActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        calendar.set(selectedYear, selectedMonth, selectedDay);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                        etDate.setText(sdf.format(calendar.getTime()));
                    },
                    year, month, day);

            // Đặt ngày tối thiểu là hôm nay
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });
    }

    private void setupTimeSlotSelection() {
        chipGroupTime.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                Chip selectedChip = findViewById(checkedIds.get(0));
                selectedTimeSlot = selectedChip.getText().toString();
            } else {
                selectedTimeSlot = "";
            }
        });
    }

    private void setupConfirmButton() {
        btnConfirmSchedule.setOnClickListener(v -> {
            // Lấy dữ liệu từ các trường
            String center = actvCenter.getText().toString();
            String date = etDate.getText().toString();

            // Kiểm tra dữ liệu
            if (center.isEmpty()) {
                Toast.makeText(this, "Please select a donation center", Toast.LENGTH_SHORT).show();
                return;
            }
            if (date.isEmpty()) {
                Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedTimeSlot.isEmpty()) {
                Toast.makeText(this, "Please select a time slot", Toast.LENGTH_SHORT).show();
                return;
            }

            // Xử lý logic đặt lịch hẹn ở đây (ví dụ: lưu vào database)
            String confirmationMessage = "Appointment confirmed!\nCenter: " + center + "\nDate: " + date + "\nTime: " + selectedTimeSlot;
            Toast.makeText(this, confirmationMessage, Toast.LENGTH_LONG).show();

            // Sau khi thành công, có thể quay về màn hình chính
            // finish();
        });
    }
}
