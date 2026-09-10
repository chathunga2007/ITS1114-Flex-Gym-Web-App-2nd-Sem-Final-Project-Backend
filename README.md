# 🏋️‍♂️ Flex Gym Management System - Backend API

[![Java](https://img.shields.io/badge/Java-21-orange.svg?logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-brightgreen.svg?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-blue.svg?logo=springsecurity&logoColor=white)](https://spring.io/projects/spring-security)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-4479A1.svg?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Build-Maven-C71A36.svg?logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

A robust, secure, and production-ready **RESTful API Backend** for the **Flex Gym Management System**, built using **Java 21**, **Spring Boot**, **Spring Data JPA**, **Spring Security**, and **MySQL**. 

This system centralizes and automates gym business operations, including member subscriptions, automated membership expiry reminders via background scheduled tasks, role-based access control (RBAC), attendance scanning, store and POS inventory management, customized workout plan assignments, and facility management (lockers, equipment, trainers).

---

## 📌 Table of Contents
- [✨ Key Features](#-key-features)
- [🛠️ Tech Stack & Dependencies](#️-tech-stack--dependencies)
- [📁 Project Architecture & Structure](#-project-architecture--structure)
- [⚙️ Getting Started & Installation](#️-getting-started--installation)
  - [Prerequisites](#prerequisites)
  - [Database Configuration](#database-configuration)
  - [SMTP Email Configuration](#smtp-email-configuration)
  - [Running the Application](#running-the-application)
- [🔐 Security & Authentication](#-security--authentication)
- [📡 API Endpoints Reference](#-api-endpoints-reference)
  - [Auth & User Management](#1-auth--user-management)
  - [Member Management](#2-member-management)
  - [Membership & Request Approvals](#3-membership-lifecycle--approvals)
  - [Attendance Tracking & QR Scan](#4-attendance-tracking)
  - [Store, Products & Categories](#5-store-products--categories)
  - [Orders & POS](#6-orders--pos)
  - [Payments & Billing](#7-payments--billing)
  - [Workout Plans & Assignments](#8-workout-plans--assignments)
  - [Facility & Operations (Trainers, Lockers, Equipment)](#9-facility--operations)
- [⏰ Automated Scheduler & Email System](#-automated-scheduler--email-system)
- [📄 Standard API Response Format](#-standard-api-response-format)
- [👥 Authors & Acknowledgments](#-authors--acknowledgments)

---

## ✨ Key Features

### 🔐 Authentication & Role-Based Access Control (RBAC)
- **JWT (JSON Web Token)** stateless authentication with BCrypt password hashing (strength 12).
- Supported user roles: `ROLE_ADMIN`, `ROLE_RECEPTIONIST`, `ROLE_MEMBER`, and `ROLE_TRAINER`.
- Secured endpoints with configurable CORS and CSRF protection.

### 👤 Member & Membership Lifecycle
- Member registration, profile updates, and status tracking (`ACTIVE`, `INACTIVE`, `SUSPENDED`).
- Membership request and approval workflow: Members can submit online membership requests; Admins/Receptionists review and approve or reject them.
- Package assignment, membership start/end dates calculation, and renewal tracking.

### ⏰ Scheduled Tasks & HTML Email Notifications
- **Spring `@Scheduled` Cron Job** running automatically every midnight (`0 0 0 * * ?`) to detect expired memberships.
- Automated 3-day advance **Membership Expiration Warning Emails** and **Expired Status Emails**.
- HTML email templates with embedded styling for:
  - Account Credentials Delivery
  - Store Order Receipts with line-item tables
  - Membership Expiration Reminders
  - Membership Expired Notices

### 📊 Attendance Tracking
- Fast QR/ID scan endpoint (`/api/attendance/scan`) for check-ins/check-outs.
- Monthly attendance breakdown and summary generation per member.

### 🛒 Gym Store, Inventory & POS
- Product inventory with category classification.
- Low-stock alert threshold query (`/api/products/getLowStockAlerts`).
- Order placement, itemized bill calculation, order status (`PENDING`, `COMPLETED`, `CANCELLED`), and automated receipt emailing.

### 💳 Payment & Billing Records
- Comprehensive payment ledger supporting `CASH`, `CARD`, `ONLINE_TRANSFER` across memberships and store orders.
- Dynamic payment status updates (`PAID`, `PENDING`, `FAILED`, `REFUNDED`).

### 🏋️ Workout Plans & Member Assignment
- Customizable workout plan creation with difficulty levels (`BEGINNER`, `INTERMEDIATE`, `ADVANCED`).
- Individualized member workout plan scheduling and tracking.

### 🏢 Facility Management
- **Trainer Management**: Trainer bio, specialization, and availability status.
- **Locker Management**: Locker allocation and state tracking (`AVAILABLE`, `OCCUPIED`, `MAINTENANCE`).
- **Equipment Management**: Inventory tracking and maintenance scheduling (`OPERATIONAL`, `UNDER_MAINTENANCE`, `RETIRED`).

---

## 🛠️ Tech Stack & Dependencies

| Technology | Purpose |
| :--- | :--- |
| **Java 21 (LTS)** | Core programming language |
| **Spring Boot 4.x / 3.x** | Application framework & dependency injection |
| **Spring Data JPA & Hibernate** | Object-relational mapping (ORM) and persistence |
| **Spring Security** | Application security and authorization |
| **JJWT (io.jsonwebtoken 0.12.3)** | JSON Web Token generation & validation |
| **MySQL 8.0+** | Relational database storage |
| **Spring Mail (JavaMailSender)** | SMTP email dispatching |
| **Lombok** | Boilerplate code reducer (Getters, Setters, Builders) |
| **Apache Maven** | Build automation and dependency management |

---

## 📁 Project Architecture & Structure

The project strictly adheres to a clean, layered architectural pattern:

```text
Flex-Gym-Management-System-Backend/
├── src/
│   ├── main/
│   │   ├── java/lk/ijse/Flex_Gym_Management_System_Backend/
│   │   │   ├── constant/         # API responses & status codes (CommonResponse, Messages)
│   │   │   ├── controller/       # REST API Controllers (14 Controllers)
│   │   │   ├── dto/              # Data Transfer Objects
│   │   │   ├── entity/           # JPA Entities (Hibernate ORM models)
│   │   │   ├── enumeration/      # Enums for statuses, roles, and types
│   │   │   ├── exception/        # Global exception handler & custom exceptions
│   │   │   ├── repository/       # Spring Data JPA Repositories
│   │   │   ├── scheduler/        # Background cron jobs (MembershipScheduler)
│   │   │   ├── security/         # SecurityConfig, JwtUtil, JwtAuthenticationFilter
│   │   │   ├── service/          # Business logic interfaces
│   │   │   │   └── impl/         # Service implementations
│   │   │   └── FlexGymManagementSystemBackendApplication.java
│   │   └── resources/
│   │       ├── css/              # External CSS for email templates (style.css)
│   │       ├── html/             # Rich HTML email templates
│   │       └── application.properties # Main application configuration
│   └── test/                     # Unit and integration test suites
├── pom.xml                       # Maven build configuration
└── README.md                     # Project documentation
```

---

## ⚙️ Getting Started & Installation

### Prerequisites
Make sure you have the following installed on your machine:
- **JDK 21** or later ([Download OpenJDK / Oracle JDK](https://adoptium.net/))
- **MySQL Server 8.0+** ([Download MySQL](https://dev.mysql.com/downloads/))
- **Apache Maven 3.9+** (or use the included `./mvnw` wrapper)
- **Git**

---

### Database Configuration
1. Open your MySQL client (MySQL Workbench, phpMyAdmin, DBeaver, or CLI).
2. Create the database:
   ```sql
   CREATE DATABASE IF NOT EXISTS flex_gym_management_system;
   ```
3. Update `src/main/resources/application.properties` with your MySQL credentials:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/flex_gym_management_system?createDatabaseIfNotExist=true
   spring.datasource.username=YOUR_MYSQL_USERNAME
   spring.datasource.password=YOUR_MYSQL_PASSWORD
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   ```

---

### SMTP Email Configuration
Configure your Gmail or custom SMTP credentials in `application.properties` to enable email notifications:
```properties
# Mail sender configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password_here
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```
> 💡 *Note: For Gmail, use an **App Password** generated from your Google Account Security settings.*

---

### Running the Application

1. **Clone the repository:**
   ```bash
   git clone https://github.com/chathunga2007/ITS1114-Flex-Gym-Management-System-2nd-Sem-Final-Project-Backend.git
   cd Flex-Gym-Management-System-Backend
   ```

2. **Build and package the project:**
   ```bash
   ./mvnw clean install
   ```

3. **Run the Spring Boot application:**
   - **Using Maven:**
     ```bash
     ./mvnw spring-boot:run
     ```
   - **Using JAR:**
     ```bash
     java -jar target/Flex-Gym-Management-System-Backend-0.0.1-SNAPSHOT.jar
     ```

The backend server starts on port `8080` by default: `http://localhost:8080`.

---

## 🔐 Security & Authentication

All requests to private endpoints must include a valid JWT token in the `Authorization` header:

```http
Authorization: Bearer <your_jwt_token_here>
```

### Public Endpoints (Permitted without Token):
- `POST /api/users/login` — User authentication and token retrieval
- `POST /api/users/saveUser` — User registration

---

## 📡 API Endpoints Reference

### 1. Auth & User Management
**Base Path:** `/api/users`

| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `POST` | `/login` | Authenticate user & receive JWT Token | ❌ No |
| `POST` | `/saveUser` | Register a new user | ❌ No |
| `PUT` | `/updateUser` | Update user details | ✅ Yes |
| `DELETE` | `/deleteUser/{userId}` | Delete user by ID | ✅ Yes |
| `GET` | `/getAllUsers` | Fetch all registered users | ✅ Yes |
| `GET` | `/getUser/{userId}` | Get user details by ID | ✅ Yes |

---

### 2. Member Management
**Base Path:** `/api/members`

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/saveMember` | Add a new gym member |
| `PUT` | `/updateMember/{memberId}` | Update member profile |
| `DELETE` | `/deleteMember/{memberId}` | Delete/deactivate member |
| `GET` | `/getAllMembers` | List all active members |
| `GET` | `/getMember/{memberId}` | Get single member profile |

---

### 3. Membership Lifecycle & Approvals
**Base Path:** `/api/memberships`

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/saveMembership` | Direct create membership (Admin/Staff) |
| `POST` | `/requestMembership` | Member online membership request |
| `PUT` | `/approveMembership/{membershipId}` | Approve pending membership request |
| `PUT` | `/rejectMembership/{membershipId}` | Reject pending membership request |
| `GET` | `/getAllPendingMemberships` | Retrieve list of all pending membership requests |
| `GET` | `/getAllMemberships` | Retrieve all active memberships |
| `GET` | `/getMembership/{membershipId}` | Retrieve membership by ID |
| `GET` | `/getMembershipsByMember/{memberId}`| List all memberships of a specific member |
| `PUT` | `/updateMembership/{membershipId}` | Update membership details |
| `DELETE` | `/deleteMembership/{membershipId}` | Delete membership record |
| `POST` | `/run-expiry-check` | Manually trigger membership expiration batch run |

---

### 4. Attendance Tracking
**Base Path:** `/api/attendance`

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/scan` | Scan QR/Barcode/ID to mark attendance |
| `GET` | `/getAllLogs` | Retrieve all attendance check-in logs |
| `GET` | `/getMemberAttendance/{memberId}` | Retrieve attendance logs for a member |
| `GET` | `/getMonthlySummary/{memberId}/{year}/{month}` | Monthly attendance summary for a member |

---

### 5. Store, Products & Categories
**Base Paths:** `/api/products` & `/api/categories`

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/categories/saveCategory` | Create product category |
| `GET` | `/api/categories/getAllCategories` | Get active product categories |
| `DELETE` | `/api/categories/deleteCategory/{id}` | Delete category |
| `POST` | `/api/products/saveProduct` | Add product to inventory |
| `PUT` | `/api/products/updateProduct` | Update product stock/details |
| `GET` | `/api/products/getAllProducts` | List active products |
| `GET` | `/api/products/getProductsByCategory/{categoryId}` | Filter products by category |
| `GET` | `/api/products/getLowStockAlerts?minStock=5` | Check low-stock inventory alerts |
| `DELETE` | `/api/products/deleteProduct/{productId}` | Delete product |

---

### 6. Orders & POS
**Base Path:** `/api/orders`

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/placeOrder` | Place new order & auto-send HTML receipt |
| `GET` | `/getOrder/{orderId}` | Get order details with items |
| `GET` | `/getAllOrders` | Retrieve all store orders |
| `GET` | `/getMemberOrders/{memberId}` | Get orders made by a member |
| `PUT` | `/updateOrderStatus/{orderId}` | Update order status and payment status |

---

### 7. Payments & Billing
**Base Path:** `/api/payments`

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/savePayment` | Record payment transaction |
| `PUT` | `/updatePaymentStatus/{id}?paymentStatus=PAID` | Update payment status |
| `GET` | `/getPayment/{id}` | Get payment by ID |
| `GET` | `/getAllPayments` | List all payment transactions |
| `GET` | `/getPaymentsByMember/{memberId}` | List payments for a specific member |
| `DELETE` | `/deletePayment/{id}` | Delete payment transaction |

---

### 8. Workout Plans & Assignments
**Base Paths:** `/api/workout-plans` & `/api/member-workout-plans`

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/workout-plans/saveWorkoutPlan` | Create workout plan template |
| `GET` | `/api/workout-plans/getAllWorkoutPlans` | List workout plan templates |
| `POST` | `/api/member-workout-plans/assignPlan` | Assign workout plan to member |
| `GET` | `/api/member-workout-plans/getAllPlans` | List all assigned member plans |
| `PUT` | `/api/member-workout-plans/updatePlan` | Update member workout assignment |
| `DELETE` | `/api/member-workout-plans/deletePlan/{id}` | Remove workout assignment |

---

### 9. Facility & Operations
**Base Paths:** `/api/packages`, `/api/trainers`, `/api/lockers`, `/api/equipments`

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` / `GET` | `/api/packages/savePackage`, `/api/packages/getAllPackages` | Gym membership package plans |
| `POST` / `GET` | `/api/trainers/saveTrainer`, `/api/trainers/getAllTrainers` | Trainer profiles & credentials |
| `POST` / `GET` | `/api/lockers/saveLocker`, `/api/lockers/getAllLockers` | Locker reservation & allocation |
| `POST` / `GET` | `/api/equipments/saveEquipment`, `/api/equipments/getAllEquipments` | Gym machinery & maintenance |

---

## ⏰ Automated Scheduler & Email System

### 1. Membership Expiry Automation
The system runs `MembershipScheduler.java` automatically every day at midnight (`0 0 0 * * ?`):
- **3 Days Prior:** Sends an automated reminder email advising the member to renew.
- **On Expiry Date:** Automatically updates membership status to `EXPIRED` and sends an expiration notification email.

### 2. Rich HTML Email Templates
Located in `src/main/resources/html/`:
- `credentials-email.html` — New user account username & password notification.
- `order-receipt.html` — POS store purchase receipt with itemized summary.
- `membership-reminder-email.html` — Expiry warning with remaining days count.
- `membership-expired-email.html` — Final expiration notice and renewal steps.

---

## 📄 Standard API Response Format

All API endpoints return responses encapsulated within the `CommonResponse` structure:

```json
{
  "code": 200,
  "data": {
    "userId": 1,
    "email": "user@flexgym.com",
    "userRole": "ROLE_ADMIN"
  },
  "message": "Operation Successful"
}
```

### Common Status Codes:
- `200` (`OPERATION_SUCCESS`): Action completed successfully.
- `500` / Custom: Application error handled gracefully by `AppExceptionHandler`.

---

## 👥 Authors & Acknowledgments

- **Developer:** [Chathunga Bimsara](https://github.com/chathunga2007)
- **Institution / Program:** IJSE (Institute of Software Engineering) - 2nd Semester Final Project
- **Project:** Flex Gym Management System

---

<p align="center">Made with ❤️ for modern gym and fitness center management.</p>
