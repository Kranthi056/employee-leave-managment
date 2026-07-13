## Secure Employee Leave Management System

A robust, role-based Leave Management System built with **Spring Boot**, featuring strong security, audit logging, and real-time monitoring using Prometheus + Grafana.


---

## Features

### Security & Compliance
- **Role-Based Access Control** (HR, Manager, Employee)
- Secure login with **account lockout policy** (5 failed attempts = 5 min lock)
- Protection against **Brute Force, XSS, and Privilege Escalation**
- Complete **Audit Trail** for all HR actions (Deactivate, Delete, Restore, Manager Assignment)
- Input sanitization using **JSoup** to prevent Stored XSS

### Core Functionality
- Employees can apply for leave
- Managers approve team leaves
- HR manages all users, leaves, and approvals
- Profile management with password change

### Observability & Monitoring
- Real-time metrics with **Prometheus**
- Beautiful dashboards in **Grafana** for:
  - System Availability
  - Throughput & Latency
  - Security Events (Failed Logins, Lockouts, XSS attempts)
  - HR Administrative Actions
- Alertmanager integration for critical alerts

### DevOps Ready
- Fully containerized with **Docker & Docker Compose**
- Multi-network architecture (DB isolated, Monitoring plane separate)



## Tech Stack

- **Backend**: Spring Boot 3, Spring Security, JDBC
- **Database**: MySQL
- **Frontend**: Thymeleaf + Bootstrap
- **Security**: Spring Security, JSoup Sanitization
- **Monitoring**: Prometheus, Grafana, Micrometer, Alertmanager
- **DevOps**: Docker, Docker Compose







