# Blood Bank Management App

A comprehensive Android application built with Java, Firebase, and SQLite to manage blood donations, requests, and inventory. The app supports multiple user roles, including Donors, Recipients, Blood Bank Staff, and Administrators.

## 🌟 Key Features

* **Multi-Role Architecture**: A customized UI and feature set for 4 distinct user roles.
* **Hybrid Database System**:
  * Uses **Firebase Authentication** for email/password validation.
  * Uses **Firebase Firestore** as the real-time, source-of-truth database for user details and blood requests.
  * Uses a local **SQLite** database (`DatabaseHelper.java`) for managing blood inventory, caching user data, and powering statistics.
* **Push Notifications**: Integrated with **Firebase Cloud Messaging (FCM)** to send and receive notifications.
* **Session Management**: Uses a `SessionManager` (SharedPreferences) to maintain user login state.
* **UI**: Built with native XML layouts and Material Components.

---

## 🔑 Functionality by Role

### 1. Administrator (Admin)
* **Hardcoded Login Email**: `admin@bloodbank.com`
* **Hardcoded Login Password**: `admin123`
* Accesses the `AdminDashboard`.
* Manages all user accounts (Donors, Recipients, Staff).
* Views and manages *all* blood requests.
* Manages blood inventory (add, update, delete units).
* Views analytics and statistics.

### 2. Blood Bank Staff
* Logs in with a registered account (role: `blood_bank_staff`).
* Accesses the `StaffDashboardActivity`.
* **Inventory Management**: Core feature to add, update, and delete blood units in the local SQLite database.
* **Request Handling**: Views and processes (approves/rejects) blood requests from recipients.
* Views a list of all available donors.

### 3. Donor
* Logs in with a registered account (role: `donor`).
* Accesses the `DonorDashboard`.
* Edits personal profile information (synced to Firestore).
* Schedules a new blood donation.
* Views active blood requests from recipients.
* Views personal donation history.

### 4. Recipient
* Logs in with a registered account (role: `recipient`).
* Accesses the `RecipientDashboard`.
* **Creates Blood Requests**: Submits new requests for blood (saved to Firestore).
* **Manages Requests**: Views the status (Pending, Approved) of their own requests.
* Searches for available donors.
* Checks current blood availability in the bank.

---

## Technology & Architecture

### Language & Platform
* **Java**: Primary programming language.
* **Android SDK**: Development platform.
* **XML**: UI layout and design.

### Database & Backend
* **Firebase Authentication**: Handles registration (`RegisterActivity`) and login (`LoginActivity`) via Email/Password.
* **Firebase Firestore**: The NoSQL real-time database, serving as the "source of truth" for:
  * User account details (in the `users` collection).
  * Blood requests (in the `requests` collection).
* **SQLite**: The local relational database (`DatabaseHelper.java`) used for:
  * Managing the **Blood Inventory** (the `blood_inventory` table).
  * Caching user data after login (the `users` table) for quick access.
  * Powering statistics (counting users, inventory, etc.).
* **Firebase Cloud Messaging (FCM)**: The `MyFirebaseMessagingService.java` class receives and displays push notifications.

### Data & Session Flow
* **SessionManager**: A utility class (`utils/SessionManager.java`) that uses SharedPreferences to store basic user info (email, role, name) and maintain the logged-in state.
* **Hybrid Data Model**:
  1.  On login (`LoginActivity.java`), the app authenticates with **Firebase Auth**.
  2.  It then fetches the user's details from **Firestore** (`users` collection).
  3.  This user data is then synced/updated in the local **SQLite** database (`DatabaseHelper.java`).
  4.  Finally, the `SessionManager` creates a session to log the user in.
  5.  When a user edits their profile (`EditProfileActivity.java`), the data is updated in **Firestore**.

---

## 🚀 Setup and Run

### Prerequisites
* Android Studio (Latest version recommended).
* A Google Account for Firebase.
* An Android device or Emulator.

### 1. Requirements

- Android Studio Hedgehog (2023.1.1 or newer)
- JDK 17

### 2. Clone the Repository

git clone https://github.com/baoquocnguyn148/BloodBankApp.git
cd BloodBankApp

3. Firebase Setup

Visit https://console.firebase.google.com/

Create a new Firebase project

Add an Android app inside Firebase Console

Package name: com.example.bloodbankapp

Download google-services.json and place it inside /app/

Go to Build → Authentication

Enable Email/Password sign-in method

4. Build & Run

Open the project in Android Studio

Allow Gradle to sync fully

Choose simulator or physical device

Click Run ▶

### Usage and Testing
The app includes a special bypass for Admin login for easy testing.

1. Admin Login (Firebase Bypass)
   To access the app with full administrative rights, use the hardcoded credentials found in LoginActivity.java:

Email: admin@bloodbank.com

Password: admin123

2. New User Registration
   Use the "Register" button on the login screen.

Create new accounts with the role of Donor or Recipient.

This data will be saved to Firebase Authentication and Firestore, then synced to the local SQLite DB upon first login.

3. Staff Login
   You must first register an account with the role blood_bank_staff.

Alternatively, log in as an Admin, go to "Manage Users", and either create a new staff user or change an existing user's role to blood_bank_staff.

📂 Key Directory Structure
src/main/
├── java/com/example/bloodbankapp/
│   ├── activities/     # (UI & Logic) Contains all Activities
│   │   ├── LoginActivity.java       # Handles login (Firebase + SQLite sync)
│   │   ├── RegisterActivity.java    # Handles registration (Firebase + Firestore)
│   │   ├── HomeActivity.java        # Launcher/dispatch activity
│   │   ├── AdminDashboard.java      # Admin dashboard
│   │   ├── DonorDashboard.java      # Donor dashboard
│   │   ├── RecipientDashboard.java  # Recipient dashboard
│   │   ├── StaffDashboardActivity.java # Staff dashboard
│   │   └── ... (Other feature activities)
│   │
│   ├── adapters/       # All RecyclerView Adapters (UserAdapter, RequestAdapter, etc.)
│   │
│   ├── database/
│   │   └── DatabaseHelper.java      # Manages the ENTIRE SQLite database (Schema, CRUD)
│   │
│   ├── models/         # POJO (Plain Old Java Objects) (User, Request, BloodUnit)
│   │
│   ├── services/
│   │   └── MyFirebaseMessagingService.java # Handles FCM push notifications
│   │
│   └── utils/
│       ├── NotificationHelper.java  # Utility for creating notification channels
│       └── SessionManager.java      # Manages login session (SharedPreferences)
│
├── res/
│   ├── layout/         # All XML layout files
│   ├── drawable/       # All icons and vector assets
│   └── values/
│       ├── strings.xml     # All string resources
│       └── themes.xml      # App theme and styles
│
└── AndroidManifest.xml   # Declares all Activities, Services, and Permissions (INTERNET,
