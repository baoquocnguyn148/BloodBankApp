// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.2" apply false // Thay "8.2.2" bằng phiên bản của bạn nếu cần
    // ✅ SỬA LỖI: Khai báo plugin google-services cho toàn bộ dự án
    id("com.google.gms.google-services") version "4.4.1" apply false
}
    