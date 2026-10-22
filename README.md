# TechPlatform

**Full Stack Microservices Application | Java 25 · Spring Boot 4.1.1 · Angular 22 · Docker**

TechPlatform is a full-stack application built with a microservices architecture, designed to demonstrate modern enterprise software development practices.

The application provides product, order, and user management through independent backend services, integrated with an Angular frontend and a centralized API Gateway.

It incorporates JWT-based authentication, role-based access control, RESTful APIs, relational and NoSQL databases, asynchronous messaging with Apache Kafka, and containerized deployment using Docker Compose.

The project also includes automated CI workflows with GitHub Actions and interactive API documentation using Swagger/OpenAPI.

## Architecture

TechPlatform follows a microservices architecture with independent backend services communicating through a centralized API Gateway.

### Core Services

| Service | Port | Responsibility |
|---|---|---|
| API Gateway | 8080 | Centralized routing and entry point for backend APIs |
| Product Service | 8081 | Product catalog and inventory management |
| Order Service | 8082 | Order processing, order items, and stock management |
| User Service | 8083 | User management, authentication, and authorization |
| Angular Frontend | 4200 | Web interface for interacting with the application |

### Infrastructure

- **Microsoft SQL Server 2022:** Relational persistence for products, orders, and users.
- **MongoDB:** NoSQL persistence integrated into the order processing architecture.
- **Apache Kafka:** Asynchronous event-driven communication.
- **Docker Compose:** Container orchestration for the complete application.
- **Nginx:** Web server for serving the Angular frontend.

The complete environment consists of eight Docker services: four backend components, the Angular frontend, SQL Server, MongoDB, and Kafka.

## Technology Stack

| Category | Technologies |
|---|---|
| Backend | Java 25, Spring Boot 4.1.1, Spring MVC, Spring WebFlux |
| Security | Spring Security, JWT, Role-Based Access Control (RBAC) |
| Microservices | Spring Cloud Gateway, REST APIs, WebClient |
| Persistence | Spring Data JPA, Hibernate, Spring Data MongoDB |
| Databases | Microsoft SQL Server 2022, MongoDB |
| Messaging | Apache Kafka |
| Frontend | Angular 22, TypeScript, HTML5, CSS3, Angular Router, Reactive Forms |
| API Documentation | Swagger UI, OpenAPI |
| Testing | JUnit, Mockito, Postman |
| Build Tools | Maven, npm |
| DevOps | Docker, Docker Compose, Nginx, GitHub Actions |
| Version Control | Git, GitHub |

### Architectural Features

- Independent microservices with clearly defined responsibilities.
- Centralized API routing through Spring Cloud Gateway.
- JWT authentication and role-based authorization.
- Synchronous REST communication between services.
- Asynchronous messaging with Apache Kafka.
- Relational and NoSQL data persistence.
- Containerized deployment of the complete application.
- Continuous integration using GitHub Actions.

## Features

### Authentication and User Management
- User authentication using JSON Web Tokens (JWT).
- Role-based access control.
- Protected API endpoints.
- User management through the Angular interface.

### Product Management
- Product catalog management.
- Product creation, retrieval, updating, and deletion (CRUD).
- Inventory and stock management.
- Product operations through REST APIs.

### Order Management
- Order creation and retrieval.
- Order items associated with products.
- Product availability and stock validation.
- Stock updates during order processing.
- Stock restoration when orders are cancelled.
- Communication with Product Service using WebClient.

### Frontend
- Angular single-page application (SPA).
- Authentication and protected routes.
- HTTP interceptor for JWT authentication.
- Interfaces for products, orders, and users.
- Integration with backend services through the API Gateway.

### API Documentation
- Interactive Swagger UI.
- OpenAPI documentation for Product, Order, and User services.
- JWT authentication support in Swagger UI.

## Getting Started

### Prerequisites

- Git
- Docker Desktop or Docker Engine with Docker Compose
- Sufficient system resources to run eight containers, including Microsoft SQL Server 2022

### 1. Clone the repository

```bash
git clone https://github.com/DaniCodesHub/TechPlatform.git
cd TechPlatform
```

### 2. Configure environment variables

Create a `.env` file from the provided template.

**Linux / macOS:**

```bash
cp .env.example .env
```

**Windows (PowerShell):**

```powershell
Copy-Item .env.example .env
```

Edit `.env` and configure the following variables:

- `DB_PASSWORD`: Strong SQL Server administrator password that meets SQL Server password complexity requirements.
- `MONGO_PASSWORD`: MongoDB administrator password.
- `MONGODB_URI`: MongoDB connection string using the configured MongoDB password.
- `JWT_SECRET`: Strong secret key of at least 32 bytes for signing and validating JWT tokens.

Ensure that the password embedded in `MONGODB_URI` matches `MONGO_PASSWORD` and is URL-encoded if it contains special characters.

**Security:** Never commit `.env` or real credentials to version control.

### 3. Start the infrastructure

Start SQL Server, MongoDB, and Apache Kafka:

```bash
docker compose up -d sqlserver mongodb kafka
```

Wait until SQL Server is ready to accept connections before proceeding.

### 4. Initialize SQL Server databases

The initialization script located at `scripts/init-databases.sql` creates the required databases if they do not already exist.

**Linux / macOS (Bash):**

```bash
docker exec -i techplatform-sqlserver /opt/mssql-tools18/bin/sqlcmd \
  -S localhost -U sa -P "$DB_PASSWORD" -No -C -b \
  < scripts/init-databases.sql
```

Load `DB_PASSWORD` from `.env` into your shell before running this command.

**Windows (PowerShell):**

```powershell
$env:DB_PASSWORD = (Get-Content .env | Where-Object { $_ -match '^DB_PASSWORD=' } | Select-Object -First 1) -replace '^DB_PASSWORD=', ''

Get-Content .\scripts\init-databases.sql -Raw | docker exec -i techplatform-sqlserver /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "$env:DB_PASSWORD" -No -C -b
```

The script initializes three databases:

- `product_db` — Product Service
- `order_db` — Order Service
- `user_db` — User Service

Database tables are managed by the corresponding backend services through their persistence configuration.

### 5. Build and start the application

```bash
docker compose up -d --build
```

Docker Compose builds the application images and starts all eight services.

Check container status:

```bash
docker compose ps
```

### 6. Access the application

| Component | URL |
|---|---|
| Angular Frontend | http://localhost:4200 |
| API Gateway | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |

The frontend communicates with the backend services through the API Gateway.

### 7. Stop the application

```bash
docker compose down
```

This stops and removes the containers while preserving persistent Docker volumes.

To restart the application:

```bash
docker compose up -d
```

**Note:** Avoid running `docker compose down -v` unless you intentionally want to delete the persistent database volumes and their data.

## API Documentation

TechPlatform provides interactive API documentation through Swagger UI and OpenAPI.

After starting the application, access:

**Swagger UI:** http://localhost:8080/swagger-ui.html

Available API documentation endpoints:

| Service | Documentation |
|---|---|
| Product Service | `/docs/products` |
| Order Service | `/docs/orders` |
| User Service | `/docs/users` |

### Authentication

Protected endpoints require a valid JSON Web Token (JWT).

1. Authenticate through the login endpoint (`/api/auth/login`).
2. Obtain the JWT access token.
3. Include the token in subsequent requests using the `Authorization: Bearer <token>` header.

Swagger UI also supports JWT authentication for testing protected endpoints.

## Continuous Integration

The project uses **GitHub Actions** to automate continuous integration workflows.

The CI configuration is maintained in the `.github/workflows/` directory.

This helps validate changes and maintain code quality throughout development.

## Project Status

**Version:** 1.0.0

TechPlatform is a portfolio project demonstrating full-stack development with Java, Spring Boot, Angular, microservices, and containerized infrastructure.

The application is designed to run locally using Docker Compose.

Future enhancements may include AI-powered product description generation using Spring AI.

## Author

**Antonio Daniel Moreno Prieto**

Full Stack Java Developer specialized in Backend Development.

- [GitHub](https://github.com/DaniCodesHub)
- [LinkedIn](https://www.linkedin.com/in/adanielmp-programadorweb)
