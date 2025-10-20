package com.example.bloodbankapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View; // Import View
import androidx.appcompat.app.AppCompatActivity;

import com.example.bloodbankapp.R;
import com.example.bloodbankapp.utils.SessionManager;
import com.google.android.material.button.MaterialButton; // Import MaterialButton

/**
 * HomeActivity giờ đây là một màn hình chính, không còn là màn hình trung chuyển.
 * Nó sẽ kiểm tra session và quyết định điều hướng hoặc ở lại.
 */
public class HomeActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private MaterialButton btnSignIn;
    // Thêm các nút khác nếu bạn muốn xử lý
    private MaterialButton btnBecomeDonor;
    private MaterialButton btnRequestBlood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sử dụng layout activity_home.xml mà bạn đã cung cấp
        setContentView(R.layout.activity_home);

        sessionManager = new SessionManager(this);

        // Ánh xạ các nút từ layout của bạn
        btnSignIn = findViewById(R.id.btn_sign_in);
        btnBecomeDonor = findViewById(R.id.btn_become_donor);
        btnRequestBlood = findViewById(R.id.btn_request_blood);

        // ✅ Gán sự kiện click cho nút "Sign In"
        btnSignIn.setOnClickListener(v -> {
            // Khi bấm nút, chuyển sang màn hình Login
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        });

        // Gán sự kiện cho các nút khác (ví dụ: chuyển đến màn hình đăng ký)
        btnBecomeDonor.setOnClickListener(v -> {
            // Chuyển đến màn hình đăng ký với vai trò là "donor"
            Intent intent = new Intent(HomeActivity.this, RegisterActivity.class);
            intent.putExtra("USER_ROLE", "donor"); // Gửi kèm vai trò để màn hình đăng ký biết
            startActivity(intent);
        });

        btnRequestBlood.setOnClickListener(v -> {
            // Yêu cầu máu có thể cần người dùng đăng nhập trước
            // Ở đây ta có thể chuyển đến màn hình đăng ký với vai trò "recipient"
            Intent intent = new Intent(HomeActivity.this, RegisterActivity.class);
            intent.putExtra("USER_ROLE", "recipient");
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Mỗi khi màn hình này được hiển thị, hãy kiểm tra session
        checkSession();
    }

    private void checkSession() {
        // Kịch bản 1: Người dùng đã đăng nhập
        if (sessionManager.isLoggedIn()) {
            // Lấy vai trò của người dùng
            String role = sessionManager.getUserRole();
            Intent intent = null;

            if (role != null) {
                switch (role) {
                    case "donor":
                        intent = new Intent(this, DonorDashboard.class);
                        break;
                    case "recipient":
                        intent = new Intent(this, RecipientDashboard.class);
                        break;
                    case "blood_bank_staff":
                        intent = new Intent(this, StaffDashboardActivity.class);
                        break;
                    case "admin":
                        intent = new Intent(this, AdminDashboard.class);
                        break;
                    default:
                        // Vai trò không hợp lệ, xóa session hỏng và ở lại HomeActivity
                        sessionManager.logoutUser();
                        // Quay lại hàm để vòng lặp sau tự xử lý (hiển thị màn hình Home)
                        return;
                }
            } else {
                // Có đăng nhập nhưng không có vai trò, xóa session hỏng
                sessionManager.logoutUser();
                return;
            }

            // Nếu có intent hợp lệ, thực hiện điều hướng
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish(); // Tự hủy HomeActivity sau khi chuyển đến Dashboard
            }
        }
        // Kịch bản 2: Người dùng CHƯA đăng nhập
        else {
            // ✅ THAY ĐỔI QUAN TRỌNG NHẤT:
            // Không làm gì cả, chỉ ở yên tại màn hình HomeActivity.
            // Người dùng sẽ phải tự bấm nút "Sign In".
        }
    }
}
