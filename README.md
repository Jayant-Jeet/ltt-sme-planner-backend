# SME Planner Backend

A Spring Boot REST API backend for the Learning & Training Team (L&TT) SME (Subject Matter Expert) Planner application. This system helps manage SME activities, schedules, and resource planning.

## Table of Contents

- [SME Planner Backend](#sme-planner-backend)
  - [Table of Contents](#table-of-contents)
  - [Features](#features)
  - [Tech Stack](#tech-stack)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Configuration](#configuration)
    - [Key Configuration Properties](#key-configuration-properties)
   - [Running the Application](#running-the-application)
      - [Using Maven](#using-maven)
      - [Using VS Code Task](#using-vs-code-task)
      - [Using JAR file](#using-jar-file)
   - [Docker](#docker)
   - [Docker Compose](#docker-compose)
  - [API Documentation](#api-documentation)
    - [Main API Endpoints](#main-api-endpoints)
  - [Database Schema](#database-schema)
  - [Authentication](#authentication)
    - [User Roles](#user-roles)
  - [Development](#development)
    - [Project Structure](#project-structure)
    - [Adding New Features](#adding-new-features)
    - [Code Style](#code-style)
  - [Contributing](#contributing)
  - [License](#license)
  - [Support](#support)
  - [Version History](#version-history)

## Features

- **User Management**: User registration, authentication, and role-based access control
- **Activity Management**: Create, update, and manage SME activities
- **Schedule Planning**: Schedule activities and manage SME availability
- **Monthly Effort Tracking**: Track and report monthly effort allocations
- **Activity Groups**: Organize activities into groups for better management
- **JWT Authentication**: Secure API endpoints with JWT tokens
- **RESTful APIs**: Well-structured REST endpoints for all operations
- **Swagger Documentation**: Interactive API documentation

## Tech Stack

- **Framework**: Spring Boot 3.5.4
- **Language**: Java 17
- **Database**: MySQL 8.0+
- **Security**: Spring Security with JWT
- **Documentation**: SpringDoc OpenAPI (Swagger)
- **Build Tool**: Maven
- **ORM**: Spring Data JPA with Hibernate

## Prerequisites

Before running the application, ensure you have:

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Git

## Installation

1. **Clone the repository**

   ```bash
   git clone <repository-url>
   cd ltt-sme-planner-backend
   ```

2. **Set up MySQL Database**

   ```sql
   CREATE DATABASE `ltt-sme-planner`;
   ```

3. **Configure Environment Variables**

   Set the following environment variables or update `application.properties`:

   ```bash
   export MYSQL_PASSWORD=your_mysql_password
   export JWT_SECRET=your_jwt_secret_key
   export JWT_EXPIRATION=86400000
   ```

## Configuration

The application uses the following configuration files:

- `application.properties`: Main configuration file
- `application-mysql.properties`: MySQL-specific configuration
- `schema.sql`: Database schema initialization
- `data.sql`: Sample data initialization

### Key Configuration Properties

```properties
# Server Configuration
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/ltt-sme-planner
spring.datasource.username=root
spring.datasource.password=${MYSQL_PASSWORD}

# JWT Configuration
jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION:86400000}

# Swagger Configuration
springdoc.swagger-ui.path=/ltt-sme-planner/v1/swagger-ui.html
```

## Running the Application

### Using Maven

1. **Build the application**

   ```bash
   mvn clean compile
   ```

2. **Run the application**

   ```bash
   mvn spring-boot:run
   ```

### Using VS Code Task

If you're using VS Code, you can use the pre-configured task:

- Open Command Palette (Ctrl+Shift+P)
- Type "Tasks: Run Task"
- Select "Build and Run Spring Boot App"

### Using JAR file

1. **Build JAR**

   ```bash
   mvn clean package
   ```

2. **Run JAR**

   ```bash
   java -jar target/sme-planner-backend-1.0.0.jar
   ```

The application will start on `http://localhost:8080`

## Docker

Build and run the service in Docker (Windows cmd examples):

```cmd
REM Build image
docker build -t ltt-sme-planner-backend:1.0.0 .

REM Run container (assumes MySQL is running on host at 3306)
docker run --name ltt-sme-planner \
   -p 8080:8080 \
   -e MYSQL_PASSWORD=your_mysql_password \
   -e JWT_SECRET=your_secret \
   -e JWT_EXPIRATION=86400000 \
   --add-host=host.docker.internal:host-gateway \
   ltt-sme-planner-backend:1.0.0
```

Notes:

- The app connects to MySQL at `localhost:3306` per `application.properties`. From inside Docker on Windows, `localhost` generally maps to the container itself; use `host.docker.internal` if connecting to the host DB.
- Override the JDBC URL at runtime with `SPRING_DATASOURCE_URL` if your DB is elsewhere, for example:

```cmd
docker run -p 8080:8080 \
   -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/ltt-sme-planner \
   -e MYSQL_PASSWORD=your_mysql_password \
   ltt-sme-planner-backend:1.0.0
```

## Docker Compose

Spin up MySQL and the app together:

```cmd
REM Optional: copy env template
copy .env.example .env

REM Build images and start services
docker compose up -d --build

REM View logs
docker compose logs -f app

REM Stop and remove services
docker compose down
```

Notes:

- MySQL data is persisted in the `db_data` volume.
- App connects to the `mysql` service via the internal network with JDBC URL set by `SPRING_DATASOURCE_URL` in `docker-compose.yml`.
- Update `.env` to customize credentials and secrets.

## API Documentation

Once the application is running, you can access:

- **Swagger UI**: <http://localhost:8080/ltt-sme-planner/v1/swagger-ui.html>
- **API Docs**: <http://localhost:8080/ltt-sme-planner/v1/api-docs>

### Main API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/auth/login` | POST | User authentication |
| `/auth/register` | POST | User registration |
| `/users` | GET/POST/PUT/DELETE | User management |
| `/activities` | GET/POST/PUT/DELETE | Activity management |
| `/schedules` | GET/POST/PUT/DELETE | Schedule management |
| `/monthly-effort` | GET/POST | Monthly effort tracking |
| `/sme-activity-groups` | GET/POST/PUT/DELETE | Activity group management |
| `/user-availability` | GET/POST/PUT/DELETE | User availability management |

## Database Schema

The application uses the following main entities:

- **Users**: SME user information and authentication
- **Activities**: Available activities for scheduling
- **Schedules**: Scheduled activities for users
- **Monthly Effort**: Monthly effort tracking
- **SME Activity Groups**: Activity grouping
- **User Availability**: User availability tracking

The database schema is automatically created from `schema.sql` and populated with sample data from `data.sql`.

## Authentication

The application uses JWT (JSON Web Tokens) for authentication:

1. **Register/Login**: Get JWT token from `/auth/login` or `/auth/register`
2. **Authorization Header**: Include token in requests:

   ```http
   Authorization: Bearer <your-jwt-token>
   ```

3. **Token Expiration**: Default expiration is 24 hours (configurable)

### User Roles

- **ADMIN**: Full access to all resources
- **SME**: Access to own resources and limited system features
- **MANAGER**: Extended access for team management

## Development

### Project Structure

```text
src/
├── main/
│   ├── java/com/edulearnorg/ltt/smeplanner/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── entity/         # JPA entities
│   │   ├── enums/          # Enumerations
│   │   ├── exception/      # Custom exceptions
│   │   ├── repository/     # Data repositories
│   │   ├── service/        # Business logic
│   │   └── util/           # Utility classes
│   └── resources/
│       ├── application.properties
│       ├── schema.sql
│       └── data.sql
└── test/                   # Test classes
```

### Adding New Features

1. Create entity in `entity/` package
2. Create repository in `repository/` package
3. Create service in `service/` package
4. Create DTOs in `dto/` package
5. Create controller in `controller/` package
6. Add tests in `test/` package

### Code Style

- Follow Java naming conventions
- Use Spring Boot best practices
- Add proper validation annotations
- Include comprehensive JavaDoc comments
- Write unit and integration tests

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/new-feature`
3. Commit changes: `git commit -am 'Add new feature'`
4. Push to branch: `git push origin feature/new-feature`
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## Support

For support and questions:

- Create an issue in the GitHub repository
- Contact the development team
- Check the API documentation for usage examples

## Version History

- **1.0.0**: Initial release with core SME planning functionality

---

Built with ❤️ by jjtomar
