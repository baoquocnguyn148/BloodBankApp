# Ứng dụng Ngân hàng máu (Blood Bank App)

![License](https://img.shields.io/badge/license-MIT-blue.svg)

Ứng dụng Ngân hàng máu là một dự án Android được xây dựng bằng Java nhằm mục đích kết nối người cần máu (Recipient) và người hiến máu (Donor) một cách nhanh chóng và hiệu quả. Quản trị viên (Admin) có thể quản lý toàn bộ hệ thống, từ người dùng đến các yêu cầu máu.

## Mục lục

- [Tính năng chính](#tính-năng-chính)
- [Đối tượng người dùng](#đối-tượng-người-dùng)
- [Công nghệ sử dụng](#công-nghệ-sử-dụng)
- [Cài đặt và Chạy dự án](#cài-đặt-và-chạy-dự-án)
- [Cấu trúc dự án](#cấu-trúc-dự-án)
- [Đóng góp](#đóng-góp)
- [Giấy phép](#giấy-phép)

## Tính năng chính

- **Xác thực người dùng:** Đăng ký, đăng nhập và quản lý phiên làm việc cho các vai trò khác nhau.
- **Quản lý theo vai trò:** Cung cấp giao diện và chức năng riêng biệt cho 3 đối tượng: Admin, Donor, và Recipient.
- **Quản lý Yêu cầu máu:**
    - Recipient có thể tạo yêu cầu máu khẩn cấp.
    - Admin có thể xem, duyệt ("Approve") hoặc từ chối ("Reject") các yêu cầu.
    - Recipient và Admin có thể xem lịch sử các yêu cầu.
- **Quản lý Người dùng:**
    - Admin có quyền xem danh sách tất cả người dùng (Donors, Recipients).
    - Admin có thể thêm, sửa, xóa thông tin người dùng.
- **Quản lý Kho máu (Inventory):**
    - Admin có thể quản lý số lượng máu hiện có theo từng nhóm máu.
    - Hiển thị thống kê trực quan về lượng máu trong kho trên Dashboard.
- **Giao diện hiện đại:** Sử dụng Material Design 3 để mang lại trải nghiệm người dùng tốt nhất.

## Đối tượng người dùng

1.  **Admin (Quản trị viên):**
    - Đăng nhập bằng tài khoản được cấp sẵn.
    - Toàn quyền quản lý người dùng, yêu cầu máu, và kho máu.
    - Xem thống kê tổng quan trên Dashboard.

2.  **Recipient (Người cần máu):**
    - Đăng ký và đăng nhập.
    - Tạo yêu cầu máu mới với các thông tin chi tiết (nhóm máu, số lượng, bệnh viện).
    - Xem trạng thái và lịch sử các yêu cầu của chính mình.
    - Xem danh sách các Donor hiện có.

3.  **Donor (Người hiến máu):**
    - Đăng ký và đăng nhập.
    - Xem danh sách các yêu cầu máu khẩn cấp từ các Recipient.
    - Cập nhật thông tin cá nhân.

## Công nghệ sử dụng

- **Ngôn ngữ:** Java
- **Kiến trúc:** Giao diện dựa trên Activity/Fragment.
- **UI:**
    - XML Layouts.
    - Material Design 3.
    - `RecyclerView` để hiển thị danh sách.
- **Lưu trữ cục bộ:**
    - `SQLite` (thông qua `SQLiteOpenHelper`) để quản lý toàn bộ dữ liệu của ứng dụng (người dùng, yêu cầu, kho máu).
- **Xác thực:**
    - Firebase Authentication (Email/Password).
- **Phụ thuộc chính:**
    - `androidx.appcompat`
    - `com.google.android.material:material`
    - `androidx.constraintlayout:constraintlayout`
    - `com.google.firebase:firebase-auth`

## Cài đặt và Chạy dự án

Để chạy dự án này trên máy của bạn, hãy làm theo các bước sau:

### 1. Yêu cầu
- Android Studio (phiên bản Hedgehog 2023.1.1 trở lên).
- Java Development Kit (JDK) 17.

### 2. Clone Repository
Mở terminal hoặc Git Bash và chạy lệnh sau để sao chép dự án về máy:

*(Lưu ý: Thay `your-username/blood-bank-app` bằng đường dẫn GitHub thực tế của bạn nếu có).*

### 3. Thiết lập Firebase
Dự án này sử dụng Firebase Authentication để quản lý đăng nhập.
1.  Truy cập [Firebase Console](https://console.firebase.google.com/) và tạo một dự án mới.
2.  Trong trang tổng quan dự án, nhấn vào biểu tượng Android để thêm một ứng dụng mới.
3.  Nhập **package name** của ứng dụng là: `com.example.bloodbankapp`.
4.  Nhấn **Register app**.
5.  Tải về file **`google-services.json`** và đặt nó vào thư mục **`app/`** của dự án trong Android Studio.
6.  Trong Firebase Console, đi đến mục **Build > Authentication**.
7.  Chọn tab **Sign-in method** và bật (Enable) phương thức **Email/Password**.

### 4. Build và Chạy
1.  Mở dự án đã clone trong Android Studio.
2.  Đợi Gradle đồng bộ hóa và tải về tất cả các thư viện cần thiết.
3.  Chọn một thiết bị (máy ảo hoặc thiết bị thật) để chạy.
    - **Khuyến nghị:** Sử dụng một máy ảo có tích hợp **Play Store** (có biểu tượng Play Store trong Device Manager) để đảm bảo các dịch vụ của Google hoạt động ổn định.
4.  Nhấn nút **Run 'app'** (biểu tượng tam giác màu xanh).

### 5. Tài khoản & Đăng nhập
- **Tài khoản Admin:** Được tạo tự động khi ứng dụng được cài đặt lần đầu.
    - **Email:** `qbaonguyen1408@gmail.com`
    - **Password:** *Hiện tại, logic đăng nhập cần được cập nhật để xử lý mật khẩu cho admin. Bạn có thể đăng ký tài khoản admin này trên Firebase Auth bằng tay, hoặc sửa code để bỏ qua kiểm tra mật khẩu cho email này.*
- **Tài khoản Donor/Recipient:**
    - Mở ứng dụng và chọn **"Sign Up"** hoặc **"Register"** để tạo tài khoản mới. Các tài khoản này sẽ được lưu vào cả Firebase Authentication và database SQLite trên thiết bị.

---

## Cấu trúc dự án
- `app/src/main/java/com/example/bloodbankapp`
    - `activities`: Chứa các Activity (màn hình) của ứng dụng.
    - `adapters`: Chứa các Adapter cho RecyclerView.
    - `database`: Chứa lớp `DatabaseHelper` để quản lý SQLite.
    - `models`: Chứa các lớp mô hình dữ liệu (User, Request, BloodUnit).
    - `utils`: Chứa các lớp tiện ích như `SessionManager`.
- `app/src/main/res`
    - `layout`: Chứa các file layout XML cho các màn hình.
    - `drawable`: Chứa các tài nguyên hình ảnh, icon.
    - `values`: Chứa các tài nguyên như màu sắc (`colors.xml`), chuỗi ký tự (`strings.xml`), và theme (`themes.xml`).

## Đóng góp
Mọi đóng góp đều được chào đón! Vui lòng tạo một `Fork` của repository này và gửi một `Pull Request` với các thay đổi của bạn.

1.  Fork the Project
2.  Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3.  Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4.  Push to the Branch (`git push origin feature/AmazingFeature`)
5.  Open a Pull Request

## Giấy phép
Dự án này được cấp phép theo Giấy phép MIT. Xem file `LICENSE` để biết thêm chi tiết.
