<<<<<<< HEAD
# Bulacan State University – College of Engineering Equipment Borrowing System (BSU-COE-EBS)

[![Platform](https://img.shields.io/badge/Platform-Android%20Native%20(Java)-green.svg)](https://developer.android.com)
[![Backend](https://img.shields.io/badge/Backend-Spring%20Boot%203.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Database](https://img.shields.io/badge/Database-MySQL%20%2F%20MariaDB-blue.svg)](https://www.mysql.com)
[![License](https://img.shields.io/badge/License-Academic%20Evaluation-orange.svg)]()

> **Bulacan State University – College of Engineering**  
> **Course / Laboratory Project:** Equipment Borrowing System  
> Developed as a native Android application based strictly on the official Figma prototype specifications.

---

## 📌 System & Submission Links

* **Software Repository:** Active GitHub repository containing complete source code, backend, database scripts, and configuration.
* **Executable Project Link (APK):** [`releases/BulSU_Equipment_Borrowing_System.apk`](releases/BulSU_Equipment_Borrowing_System.apk) (Directly installable on any Android phone or Android Studio emulator).
* **Live Web Application / Backend API:** `http://localhost:8080/` (Spring Boot Web Portal & REST API).

---

## 📖 System Overview

The **BulSU COE Equipment Borrowing System** is an end-to-end laboratory inventory management and equipment loan tracking platform. Built for students, faculty, and laboratory technicians of the College of Engineering, it streamlines borrowing, tracks stock availability in real time, validates loan durations, and maintains complete accountability across laboratory transactions.

### Key Objectives Achieved:
* **Figma Fidelity:** Recreates every screen from the Figma prototype with pixel-level precision, including BulSU typography, colors, serif headers, custom vector drawables, bottom navigation, and badge states.
* **Dynamic Database Counts:** All counts (`67 Available Equipment`, `2 My Requests`, `5 Borrowed`, `2 Pending`, `3 Returned`) are dynamically computed from MySQL/MariaDB.
* **Role-Based Access Control:** Distinct workflows and security separation for **Borrowers** (students/faculty) and **Lab Administrators**.
* **Fault-Tolerant Offline Architecture:** Incorporates repository fallbacks so the mobile application continues to display complete data and interactive functions even if the backend server is restarting.

---

## 🏛️ System Architecture

```mermaid
flowchart TD
    subgraph MobileApp ["Android Mobile Application (Java 17, Target SDK 34)"]
        UI["XML Layouts & Material Views"]
        ADAPT["RecyclerView Adapters"]
        REPO["Repository Layer (Offline-First Fallback)"]
        RETRO["Retrofit 2 REST Client"]
        UI --> ADAPT
        ADAPT --> REPO
        REPO --> RETRO
    end

    subgraph Backend ["Java Spring Boot REST API (Port 8080)"]
        CTRL["REST Controllers (/api/auth, /api/equipment, /api/borrow, /api/dashboard)"]
        SVC["Service Layer & Transaction Logic"]
        JPA["Spring Data JPA Repositories"]
        CTRL --> SVC
        SVC --> JPA
    end

    subgraph Database ["MySQL / MariaDB (bulsu_equipment_db)"]
        USERS[("users")]
        CAT[("categories")]
        EQUIP[("equipment")]
        TX[("borrow_transactions")]
        NOTIF[("notifications")]
        JPA --> Database
    end

    RETRO -- "HTTP JSON (10.0.2.2:8080)" --> CTRL
```

---

## 🎨 Figma Prototype Screens & Implementation Mapping

Every screen is mapped directly to the Figma prototype exported in `Lab_5.pdf`:

| Figma Page | Screen Name | Layout File | Java Component | Key Features & Usability |
| :--- | :--- | :--- | :--- | :--- |
| **Page 28** | **Login** | `activity_login.xml` | `LoginActivity.java` | BulSU 3D Cube isometric vector logo, bold serif branding, password visibility toggle, role-based login routing, BulSU building facade. |
| **Page 18** | **Sign Up** | `activity_signup.xml` | `SignUpActivity.java` | Student ID, password with eye toggle, confirm password validation, duplicate student ID prevention. |
| **Pages 19, 29, 33, 38** | **Home Dashboard** | `fragment_home.xml` | `HomeFragment.java` | BulSU gold building header, personalized student greeting, live dynamic counters (`67 Available`, `2 My Requests`), **interactive category selector with gold active states**, dynamic featured equipment slider. |
| **Pages 20, 30, 34, 39** | **Equipment Catalog** | `fragment_catalog.xml` | `CatalogFragment.java` | Real-time immediate search filtering, category filter pills (`ALL`, `Tools & Repairs`, `Electronics & Components`, `Testing & Management`, `Others`), consistent single-category database structure. |
| **Page 21** | **Borrow Equipment** | `activity_borrow.xml` | `BorrowActivity.java` | White header with circular back button, Dark Navy container, equipment card with green `Available: 26` badge below image, interactive `[-] 1 [+]` quantity stepper bounded to stock, DatePickers, live borrowing summary. |
| **Pages 22, 31, 32, 35, 36, 37, 40, 41** | **Borrow History** | `fragment_history.xml` | `HistoryFragment.java` | Filter pills (`All`, `Pending`, `Approved`, `Returned`, `ooo`), color-coded badges, clickable card with chevron, persistent filter state on back navigation. |
| **Requirement 4.E** | **Request Details** | `activity_request_detail.xml` | `RequestDetailActivity.java` | Full request breakdown: Transaction ID, loan period, item condition, accessories, admin remarks, and direct "Proceed to Return Equipment" button. |
| **Page 23** | **Return Equipment** | `activity_return.xml` | `ReturnActivity.java` | Validates approved loan status, return date selector, condition checklist, accessories completeness dropdown, return remarks, inventory restoration. |
| **Page 24** | **Profile** | `fragment_profile.xml` | `ProfileFragment.java` | Circular user avatar, student details (`2024101234 BS Computer Engineering, CpE-3A`), dynamic loan summary (`5 Borrowed`, `2 Pending`, `3 Returned`), editable user information, logout. |
| **Screen 8 / View 2** | **System Feedback & Modal View** | `dialog_system_feedback.xml`<br>`dialog_confirm_borrow.xml`<br>`dialog_admin_remarks.xml`<br>`dialog_notifications.xml` | `FeedbackDialogHelper.java` | **View 2 (Figure 2) in CPE 304L Rubric:** (1) Stock limit alert modal (triggered by FR-003 when hitting max stock), (2) Loan confirmation popup with full borrowing summary, (3) Request submitted success modal with generated Transaction ID, (4) Validation error modals, (5) Administrator rejection modal with required remarks, (6) System status change notifications. |
| **Requirement 3.B** | **Lab Admin Panel** | `activity_admin_dashboard.xml` | `AdminDashboardActivity.java` | Administrator control panel for reviewing loan requests, single-click Approve / Reject with remarks, and inventory management. |

---

## 🔑 Test Credentials & User Roles

| Role | Student ID / Username | Password | Accessible Features |
| :--- | :--- | :--- | :--- |
| **Borrower (Student)** | `2024101234` | `JuAnPoGi123` | Equipment Catalog, Borrow Requests, Loan Stepper, Request History, Profile, Return Requests |
| **Lab Administrator** | `admin2026` | `Admin@123` | Admin Dashboard, Request Approvals/Rejections, Stock In/Out Verification, Transaction Auditing |

---

## 🛠️ Technology Stack

* **Mobile Framework:** Native Android (Java 17)
* **Compile / Target SDK:** API 34 (Android 14)
* **Minimum SDK:** API 24 (Android 7.0 Nougat)
* **UI Styling:** Native XML layouts, CSS-like modular styles, shapes, and scalable Vector Drawables
* **Networking:** Retrofit 2.9 + OkHttp3 + Gson Converter
* **Backend Framework:** Java Spring Boot 3.2.5
* **Persistence & ORM:** Spring Data JPA + Hibernate + MySQL Connector/J
* **Database:** MySQL / MariaDB (Port 3306)
* **Build Systems:** Gradle 8.2 (Android) & Apache Maven 3.8 (Backend)

---

## 🗄️ Database Schema & Initial Data

The system uses the relational database `bulsu_equipment_db`:

1. **`users`**: User accounts, student IDs, encrypted passwords, roles (`BORROWER`, `ADMIN`), course, section, and contact info.
2. **`categories`**: Standardized equipment classifications (`Tools & Repairs`, `Electronics & Components`, `Testing & Management`, `Others`).
3. **`equipment`**: Inventory items, unique item codes (`EQ-001` to `EQ-010`), total vs. available quantities, condition, included accessories, and image references.
4. **`borrow_transactions`**: Loan records, unique transaction IDs (`TRX-YYYYMMDD-XXX`), dates, statuses (`PENDING`, `APPROVED`, `RETURNED`, `REJECTED`, `OVERDUE`), accessories checked, and admin remarks.
5. **`notifications`**: Activity alerts and loan status updates for borrowers.

---

## 🚀 Setup & Execution Guide

### Option 1: Direct APK Installation (Fastest for Professor Evaluation)

An executable debug APK is pre-built and included in the repository:
* **Location:** [`releases/BulSU_Equipment_Borrowing_System.apk`](releases/BulSU_Equipment_Borrowing_System.apk)
* **Installation via ADB:**
  ```powershell
  adb install -r "releases/BulSU_Equipment_Borrowing_System.apk"
  ```
* **Or drag-and-drop** the `.apk` file directly onto any running Android Emulator or transfer it to an Android phone.

---

### Option 2: Open and Run in Android Studio

1. Open **Android Studio**.
2. Select **File → Open** and choose the `android/` directory:
   ```
   bulsu-equipment-borrowing-system/android
   ```
3. Allow Gradle to sync dependencies.
4. Connect an Android device (with **USB Debugging** enabled) or start an AVD emulator (`Medium Phone API 37.0` or similar).
5. Click the green **Run (▶)** button on the top toolbar (or press `Shift + F10`).

---

### Option 3: Starting the Backend & Database (Optional for Live API Sync)

1. **Import Database:**
   Ensure MySQL/MariaDB is running on port 3306, then execute:
   ```cmd
   import_database.bat
   ```
   *(Or import `database/bulsu_equipment_db.sql` via phpMyAdmin / MySQL CLI).*

2. **Start Spring Boot Backend:**
   Run the backend start script:
   ```cmd
   run_backend.bat
   ```
   *(The server runs on `http://localhost:8080/` with web portal and REST endpoints).*

---

## 📡 REST API Reference

| Method | Endpoint | Description | Role |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Authenticate borrower or lab administrator | Public |
| `POST` | `/api/auth/register` | Register new student borrower | Public |
| `GET` | `/api/auth/profile/{userId}` | Retrieve profile information | Borrower |
| `GET` | `/api/dashboard/stats/{userId}` | Dynamic available equipment & requests count | Borrower |
| `GET` | `/api/equipment` | Filter equipment catalog by search & category | Public |
| `GET` | `/api/equipment/featured` | Fetch featured equipment | Public |
| `POST` | `/api/borrow` | Submit a new equipment borrow request | Borrower |
| `POST` | `/api/borrow/return` | Submit equipment return request | Borrower |
| `GET` | `/api/borrow/user/{userId}` | Get user transaction history with filter | Borrower |
| `GET` | `/api/admin/transactions` | View all borrowing transactions | Admin |
| `POST` | `/api/admin/transactions/{id}/approve` | Approve request and deduct stock | Admin |
| `POST` | `/api/admin/transactions/{id}/reject` | Reject request with administrator remarks | Admin |

---

## 📁 Repository Directory Structure

```
bulsu-equipment-borrowing-system/
├── .gitignore                          # Git exclusions for build, caches, and IDE configs
├── README.md                           # Complete project documentation and professor guide
├── import_database.bat                 # Script to import MySQL database schema and seeds
├── run_backend.bat                     # Script to start Spring Boot REST API
│
├── releases/                           # Pre-compiled executable binaries
│   └── BulSU_Equipment_Borrowing_System.apk # Executable Android APK (6.6 MB)
│
├── android/                            # Android Studio Native Java Project
│   ├── build.gradle                    # Project-level Gradle configuration
│   ├── settings.gradle                 # Module settings
│   ├── gradlew.bat                     # Gradle wrapper executable
│   └── app/
│       ├── build.gradle                # App-level dependencies (Retrofit, Glide, Gson)
│       └── src/main/
│           ├── AndroidManifest.xml     # App permissions, activities, launcher declaration
│           ├── java/edu/bulsu/equipmentborrowing/
│           │   ├── model/              # User, Equipment, Category, BorrowTransaction models
│           │   ├── network/            # Retrofit2 ApiClient & ApiService
│           │   ├── repository/         # EquipmentRepository, BorrowRepository
│           │   ├── ui/                 # Activity and Fragment controllers
│           │   └── utils/              # SessionManager, UiUtils
│           └── res/                    # Drawables, XML layouts, strings, colors, styles
│
├── backend/                            # Spring Boot Java REST API
│   ├── pom.xml                         # Maven dependencies
│   └── src/main/
│       ├── java/edu/bulsu/equipment/backend/ # Controllers, Services, Repositories, DTOs
│       └── resources/
│           ├── application.properties  # Database JDBC configuration
│           └── static/index.html       # Web Portal for browser testing
│
└── database/                           # Relational Database SQL Script
    └── bulsu_equipment_db.sql          # DDL Schema + Seed data (users, equipment, loans)
```

---

## 👥 Contributors

* **Bulacan State University** – College of Engineering
* **Project:** Equipment Borrowing System (BSU-COE-EBS)
=======
# bulsu-equipment-borrowing-system
A mobile-based equipment borrowing management system for Bulacan State University College of Engineering. Developed using Java, Spring Boot, MySQL, and Android to manage equipment inventory, borrowing requests, approvals, returns, and transaction records through a centralized platform.
>>>>>>> f60a1f943001a04fa8b6bf3cf59b8fb61cfbe0f3
