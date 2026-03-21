<div align="center">
  <h1>🐝 BusyBee</h1>
  <p><b>A secure task management web application built with Java Spring Boot.</b></p>
</div>

The project focuses on both **functionality** and **security**, including protected task operations, secure file uploads, CSRF protection, role-based authorization, and safe handling of user input.

---

## 🚀 Features

### 🔐 Authentication & Authorization
* User registration with **hashed passwords**
* Login with Spring Security
* Role-based access control
* CSRF protection for sensitive POST requests
* Task-level authorization using `@PreAuthorize` and custom authorization logic

### 📝 Task Management
* Create tasks
* Mark tasks as done
* Assign task responsibility to specific users
* Prevent duplicate task names
* Validate task due date and due time
* Enforce restrictions for trial users

### 📎 Comments & Uploads
* Add comments to tasks
* Upload image or attachment files
* Download attachments securely
* View images securely
* Support image insertion via **remote link**
* Server downloads linked images and stores them locally

### 🛡️ Security Mechanisms
* Password hashing using Spring Security
* Protected `/done` and `/create` endpoints
* HTML sanitization for task descriptions
* XSS prevention
* Validation of task names, comments, dates, times, and assigned users
* Secure filename validation & Path traversal protection
* Content-Type enforcement for images and attachments
* Redundant defenses for image serving
* CSRF token generation and validation
* Protection against unsafe remote image downloads
* File size limits for remote images
* Disk-space checks before saving uploaded content

---

## 🛠️ Technologies Used

![Java](https://img.shields.io/badge/Java%2021-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)
![JUnit 5](https://img.shields.io/badge/JUnit_5-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![HTML/CSS/JS](https://img.shields.io/badge/HTML_CSS_JS-E34F26?style=for-the-badge&logo=html5&logoColor=white)

* **Jackson** (JSON processing)
* **OWASP Java HTML Sanitizer**

---

## 📂 Project Structure

```text
src/
├── main/
│   ├── java/com/securefromscratch/busybee/
│   │   ├── auth/          # authentication and authorization classes
│   │   ├── config/        # security configuration
│   │   ├── controllers/   # REST controllers
│   │   ├── safety/        # validated input wrapper classes
│   │   └── storage/       # task and file storage logic
│   └── resources/
│       └── static/        # frontend files
└── test/
    └── java/com/securefromscratch/busybee/
        └── safety/        # unit tests for safety/input validation



💻 Getting Started
Prerequisites
Java 21 JDK

Gradle

Running the Application
Clone the repository.

Build the project using Gradle: ./gradlew build

Run the application: ./gradlew bootRun

Access the web interface at http://localhost:8080.
