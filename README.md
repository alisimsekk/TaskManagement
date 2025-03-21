# Task Management System

![Java](https://img.shields.io/badge/Java-21-yellow)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-green)
![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-red)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16.8-blue)

## Overview

The Task Management System is an advanced application developed for Lorem Ipsum corporation to replace their legacy task manager. This modern solution provides comprehensive project and task management capabilities with team collaboration features.

## 📌 Features

- **Project Management**: Create and manage projects associated with departments
- **Task Management**: Manage tasks within projects with detailed information
- **Team Member Assignment**: Assign team members to tasks
- **Progress Tracking**: Track task progress with different states (Backlog, In Analysis, In Development, Completed, Cancelled, Blocked)
- **Priority Management**: Assign priorities to tasks (Critical, High, Medium, Low)
- **File Attachment Support**: Attach files to tasks
- **Comments**: Add comments to tasks for improved collaboration
- **Role-Based Access Control**: Different permissions for Project Managers, Team Leaders, and Team Members

## 🛠 Technology Stack

| Component | Version |
|-----------|---------|
| Java | 21 |
| Spring Boot | 3.4.3 |
| Spring Security | JWT Authentication |
| PostgreSQL | 16.8 |
| Spring Data JPA | - |
| Querydsl | 5.1.0 |
| OpenAPI (Swagger) | 2.8.5 |
| Maven | - |
| Docker | - |

## 📂 Project Structure

```
src/
├── main/
│   ├── java/com/alisimsek/taskmanagement/
│   │   ├── auth/               # Authentication controllers and DTOs
│   │   ├── attachment/         # File attachment functionality
│   │   │   ├── controller/
│   │   │   ├── entity/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   ├── comment/            # Task comments
│   │   │   ├── controller/
│   │   │   ├── entity/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   ├── common/             # Common utilities and base classes
│   │   │   ├── base/
│   │   │   └── exception/
│   │   ├── config/             # Application configuration
│   │   ├── department/         # Department management
│   │   │   ├── controller/
│   │   │   ├── entity/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   ├── initializer/        # Data initialization
│   │   ├── project/            # Project management
│   │   │   ├── controller/
│   │   │   ├── entity/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   ├── role/               # User roles and permissions
│   │   │   ├── entity/
│   │   │   └── repository/
│   │   ├── security/           # Security configuration
│   │   ├── task/               # Task management
│   │   │   ├── controller/
│   │   │   ├── entity/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   ├── user/               # User management
│   │   │   ├── controller/
│   │   │   ├── entity/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   └── TaskManagementApplication.java
│   │
│   └── resources/
│       ├── application.yml
│       ├── application-docker-local.yml
│       ├── exceptions.properties
│
├── test/
│   └── java/com/alisimsek/taskmanagement/
│       ├── attachment/
│       ├── comment/
│       ├── department/
│       ├── project/
│       ├── role/
│       ├── task/
│       └── user/
│
├── docker/
│   └── local/
│       └── docker-compose.yml  # Local development environment
│
└── uploads/                   # Storage for uploaded files
```

## ⚙️ Running the Application

### Prerequisites

- Java 21 or higher
- Maven
- Docker (optional)

### Using Docker

```bash
cd docker/local
docker-compose up -d
```

Then run the application with the docker-local profile:

```bash
mvn spring-boot:run -Dspring.profiles.active=docker-local
```

### Manual Setup

1. Set up a PostgreSQL database
2. Update application properties with your database credentials
3. Run the application:

```bash
mvn spring-boot:run
```

## 👤 Default Users

The system initializes with the following default users:

| Username | Password | User Type | Role | Permissions |
|----------|----------|-----------|------|------------|
| admin@mail.com | Aa123456 | ADMIN | super-admin-role | MANAGE_PROJECTS, MANAGE_TASKS, ASSIGN_TEAM_MEMBER, UPDATE_TASK_STATE, SET_TASK_PRIORITY, ADD_ATTACHMENT |
| project-manager@mail.com | Aa123456 | PROJECT_MANAGER | super-pm-role | MANAGE_PROJECTS, MANAGE_TASKS, ASSIGN_TEAM_MEMBER, UPDATE_TASK_STATE, SET_TASK_PRIORITY, ADD_ATTACHMENT |
| team-leader@mail.com | Aa123456 | TEAM_LEADER | super-tl-role | MANAGE_PROJECTS, MANAGE_TASKS, ASSIGN_TEAM_MEMBER, UPDATE_TASK_STATE, SET_TASK_PRIORITY, ADD_ATTACHMENT |
| team-member@mail.com | Aa123456 | TEAM_MEMBER | super-tm-role | UPDATE_TASK_STATE, ADD_ATTACHMENT |

## 📡 API Documentation

API documentation is available via Swagger UI at `/swagger-ui.html` when the application is running.

A Postman collection is also provided in the `postman-collection` directory.

## 💡 Business Rules

### Task State Transitions

- **Happy Path**: Backlog ⇔ In Analysis ⇔ In Development/Progress ⇔ Completed
- **Cancel Path**: Any state except Completed can be changed to Cancelled
- **Blocked Paths**: In Analysis ⇔ Blocked, In Development/Progress ⇔ Blocked
- Reason must be provided when a task is Cancelled or Blocked
- Completed tasks cannot be moved back to any other state

### Access Control

- Team members (except Team Leader or Project Manager) cannot change the description and title of a task
- Team members can change attachments, state, and add comments to tasks