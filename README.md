# 📚 Learning Management System (LMS)

A backend Learning Management System built with **Java** and **Spring Boot**, designed to manage courses, users, and academic operations through a secure, RESTful API.

## Overview

This project provides the server-side foundation for an LMS platform — handling authentication, data persistence, notifications, and reporting so a web or mobile frontend can be built on top of it.

## ✨ Features

- 🔐 **Secure Authentication & Authorization** — JWT-based login with Spring Security
- 🗃️ **Data Persistence** — Spring Data JPA with a SQLite database
- 📧 **Email Notifications** — built-in mail support via Spring Boot Starter Mail
- 📊 **Excel Import/Export** — generate and read `.xlsx` reports using Apache POI
- ✅ **Request Validation** — enforced with Spring Boot Starter Validation
- 🧪 **Tested** — unit and security test coverage with Spring Boot Test & Spring Security Test

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.4.0 |
| Security | Spring Security + JWT (`jjwt`) |
| Persistence | Spring Data JPA, Hibernate (Community Dialects), SQLite |
| Reporting | Apache POI |
| Mail | Spring Boot Starter Mail |
| Boilerplate | Lombok |
| Build Tool | Maven (with Maven Wrapper) |

## 📋 Prerequisites

Before running the project, make sure you have:

- **Java 17** or later ([download](https://adoptium.net/))
- **Maven** (or use the included `mvnw` / `mvnw.cmd` wrapper — no local install needed)
- An SMTP account/credentials if you want email notifications to work

## 🚀 Getting Started

1. **Clone the repository**
   ```bash
   git clone https://github.com/OmarAbdelmonemSayed/Learning-Management-System.git
   cd Learning-Management-System
   ```

2. **Configure the application**

   Update `src/main/resources/application.properties` (or `application.yml`) with your own settings, for example:
   ```properties
   # Database
   spring.datasource.url=jdbc:sqlite:lms.db
   spring.datasource.driver-class-name=org.sqlite.JDBC
   spring.jpa.hibernate.ddl-auto=update

   # JWT
   jwt.secret=your-secret-key
   jwt.expiration=86400000

   # Mail
   spring.mail.host=smtp.gmail.com
   spring.mail.port=587
   spring.mail.username=your-email@example.com
   spring.mail.password=your-app-password
   ```

3. **Run the application**

   Using the Maven wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```
   On Windows:
   ```bash
   mvnw.cmd spring-boot:run
   ```

   The API will start on `http://localhost:8080` by default.

4. **Run the tests**
   ```bash
   ./mvnw test
   ```

## 📁 Project Structure

```
Learning-Management-System/
├── .mvn/wrapper/          # Maven wrapper files
├── src/
│   ├── main/
│   │   ├── java/          # Application source code
│   │   └── resources/     # Configuration files
│   └── test/               # Unit & integration tests
├── pom.xml                 # Project dependencies & build config
├── mvnw / mvnw.cmd          # Maven wrapper scripts
└── README.md
```

## 👥 Team

- Omar Abdelmonem Sayed Mohamed
- Ahmed Mohamed Amer Ahmed
- Ahmed Ehab Shehata Ali
- Mohanad Abdullrahem Abdullrahman Ahmed
- Mohamed Abdelwahab Mostafa Mohamed
- Ahmed Hossam Samir ElAlfy
