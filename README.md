## Blood Bank Application (Blood Bank App)

## The Blood Bank App is an Android project built with **Java**, designed to connect blood recipients (patients in need of blood) with blood donors quickly and efficiently. An **Admin** manages the entire system including users, blood requests, and blood inventory.

## Table of Contents

- [Features](#features)
- [User Roles](#user-roles)
- [Technologies Used](#technologies-used)
- [Installation & Setup](#installation--setup)
- [Project Structure](#project-structure)

## Features

- **User Authentication** via Firebase Authentication (Email/Password).
- **Role-based Access Control** for Admin, Donor, and Recipient.
- **Blood Request Management:**
    - Recipients can create emergency blood requests.
    - Admin can approve or reject requests.
    - Request history is available for both Admin and Recipient.
- **User Management (Admin):**
    - View, add, edit and delete Donors / Recipients.
- **Blood Inventory Management:**
    - Admin can update blood units by blood type.
    - Inventory overview displayed on Admin Dashboard.
- **Modern UI:** Built with Material Design 3 and RecyclerView.

---

## 👥 User Roles

| Role          |  Description                                                  |
|---------------|---------------------------------------------------------------|
| **Admin**     | Manages users, reviews blood requests, updates inventory.     |
| **Recipient** | Creates blood requests and tracks request status/history.     |
| **Donor**     | Views nearby/urgent blood requests and manages personal info. |

---

##  Technologies Used

| Component      | Details                         |
|----------------|---------------------------------|
| Language       | Java                            |
| Architecture   | Activity / Fragment             |
| UI             | XML Layout + Material Design 3  |
| Local Storage  | SQLite using `SQLiteOpenHelper` |
| Authentication | Firebase Authentication         |
| AndroidX       | AppCompat + ConstraintLayout    |
| Google         | Material Components             |

---

## Installation & Setup

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


📂 Project Structure
app/src/main/java/com/example/bloodbankapp
│
├── activities       # Screens / Activities
├── adapters         # RecyclerView Adapters
├── database         # DatabaseHelper (SQLite)
├── models           # User, Request, Inventory models
└── utils            # SessionManager, constants, helpers

app/src/main/res
├── layout           # XML Layout files
├── drawable         # Icons & graphic assets
└── values           # colors.xml, strings.xml, themes.xml
