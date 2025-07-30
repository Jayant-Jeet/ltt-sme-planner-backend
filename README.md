# L&TT Planner Backend

A Spring Boot REST API for managing user schedules in the Learning & Talent Transformation team at EduLearnOrg with role-based access control.

## Features

- **Role-Based Access Control**: Support for three user roles - SME, Supervisor, and Lead
- **User Management**: Create, update, and manage users with different roles
- **Activity Management**: Manage different types of training activities with categories and durations
- **SME Activity Grouping**: Track and analyze SME activities by categories with automatic grouping
- **Activity Categories**: Support for predefined activity categories (Calendar Training, Blended, etc.)
- **Duration Handling**: Fixed and variable duration activities with automatic time tracking
- **Schedule Management**: Create, update, and delete user schedules
- **Availability Search**: Find available users for specific dates and times
- **Conflict Detection**: Automatic detection of scheduling conflicts
- **Analytics**: Monthly activity distribution and performance metrics for SMEs
- **RESTful API**: Full REST API following OpenAPI 3.0 specification
- **JWT Authentication**: Secure authentication with JSON Web Tokens

## Activity Categories

The system supports the following predefined activity categories with specific durations:

### Fixed Duration Activities

- **Lateral Calendar Training Full Day**: 9.0 hours
- **Lateral Calendar Training Half Day**: 4.5 hours  
- **Blended Learning**: 2.0 hours
- **Adhoc Training**: 4.5 hours
- **Byte sized**: 2.0 hours

### Variable Duration Activities

- **Content Development**: Variable duration based on requirements
- **Evaluation**: Variable duration based on assessment needs
- **Skill Upgrade**: Variable duration based on training scope
- **Program and Session Planning**: Variable duration for management activities
- **Time Off Categories**: Public Holiday, Leave, Location Holiday, Optional Holiday

## SME Activity Grouping

The system automatically groups SME activities by:

- **Category**: Activities are grouped by their predefined categories
- **Monthly Tracking**: Activities are tracked on a monthly basis (YYYY-MM format)
- **Performance Metrics**: Total hours allocated, session counts, and analytics
- **Automatic Processing**: When schedules are created for SMEs, activity groupings are automatically updated

## User Roles

### SME (Subject Matter Expert)

- Can view activities
- Can manage their own schedules
- Can view their own profile

### Supervisor

- All SME permissions
- Can view all SMEs
- Can view SME schedules
- Can assign activities to SMEs

### Lead

- All Supervisor permissions
- Can manage all users (create, update, delete)
- Can change user roles
- Can view all system data
- Full administrative access

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring Security with JWT**
- **H2 Database** (in-memory for development)
- **MySQL** (for production)
- **Maven** for dependency management

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Run the application:

   ```bash
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080`

### Database Access

- **H2 Console**: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: `password`

## API Endpoints

### Authentication

- `POST /ltt-sme-planner/v1/auth/login` - User login (returns JWT token)

### Activities

- `GET /ltt-sme-planner/v1/activities` - Get all activities (All roles)
- `POST /ltt-sme-planner/v1/activities` - Create new activity (Lead only)
- `PUT /ltt-sme-planner/v1/activities/{id}` - Update activity (Lead only)
- `DELETE /ltt-sme-planner/v1/activities/{id}` - Delete activity (Lead only)

### SME Activity Grouping API

- `GET /ltt-sme-planner/v1/sme-activities/sme/{smeUserId}` - Get all activity groups for an SME
- `GET /ltt-sme-planner/v1/sme-activities/sme/{smeUserId}/month/{monthYear}` - Get activity groups for SME and month
- `GET /ltt-sme-planner/v1/sme-activities/sme/{smeUserId}/distribution/{monthYear}` - Get activity distribution by category
- `GET /ltt-sme-planner/v1/sme-activities/sme/{smeUserId}/total-hours/{monthYear}` - Get total hours for SME and month
- `GET /ltt-sme-planner/v1/sme-activities/sme/{smeUserId}/category/{category}/hours/{monthYear}` - Get hours by category
- `GET /ltt-sme-planner/v1/sme-activities/activities/category/{category}` - Get activities by category
- `GET /ltt-sme-planner/v1/sme-activities/activities/fixed-duration` - Get fixed duration activities
- `GET /ltt-sme-planner/v1/sme-activities/activities/variable-duration` - Get variable duration activities
- `GET /ltt-sme-planner/v1/sme-activities/month/{monthYear}/active-smes` - Get active SMEs for a month
- `GET /ltt-sme-planner/v1/sme-activities/categories` - Get all activity categories

### User Management

- `GET /ltt-sme-planner/v1/users` - Get all users (Lead only)
- `GET /ltt-sme-planner/v1/users/smes` - Get all SMEs (Supervisor, Lead)
- `GET /ltt-sme-planner/v1/users/supervisors` - Get all Supervisors (Lead only)
- `GET /ltt-sme-planner/v1/users/{id}` - Get user by ID (Supervisor, Lead)
- `POST /ltt-sme-planner/v1/admin/users` - Create new user (Lead only)
- `PUT /ltt-sme-planner/v1/admin/users/{id}/role` - Update user role (Lead only)
- `DELETE /ltt-sme-planner/v1/admin/users/{id}` - Delete user (Lead only)

### Schedules (Future endpoints)

- `GET /ltt-sme-planner/v1/schedules?month=YYYY-MM` - Get schedules by month (for current user)
- `GET /ltt-sme-planner/v1/schedules/sme/{smeId}?month=YYYY-MM` - Get schedules for any SME by ID
- `POST /ltt-sme-planner/v1/schedules` - Create new schedule
- `PUT /ltt-sme-planner/v1/schedules/{id}` - Update schedule
- `DELETE /ltt-sme-planner/v1/schedules/{id}` - Delete schedule

### SME Management

- `GET /ltt-sme-planner/v1/smes/by-email?email={email}` - Get SME details by email

### SME Availability

- `GET /ltt-sme-planner/v1/smes/availability?date=YYYY-MM-DD&fromTime=HH:MM:SS&toTime=HH:MM:SS` - Search available SMEs
- `GET /ltt-sme-planner/v1/smes/{id}/availability?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD` - Get SME availability

## Sample Data

The application loads predefined activities automatically including:

### Predefined Activities with Categories and Durations

- **Lateral Calendar Training Full Day** (Calendar Training - 9.0 hours)
- **Lateral Calendar Training Half Day** (Calendar Training - 4.5 hours)
- **Blended Learning** (Blended - 2.0 hours)
- **Adhoc Training** (Adhoc Training - 4.5 hours)
- **Byte sized** (Byte Sized - 2.0 hours)
- **Content Development** (Content Development - Variable)
- **Evaluation** (Evaluation - Variable)
- **Skill Upgrade** (Skill Upgrade - Variable)
- **Program and Session Planning** (Management - Variable)
- **Public Holiday** (Time Off - Variable)
- **Leave** (Time Off - Variable)
- **Location Holiday** (Time Off - Variable)
- **Optional Holiday** (Time Off - Variable)

### Sample Users

- 6 users with different roles (SME, Supervisor, Lead)
- Sample schedules for testing SME activity grouping

## API Authentication

**Note**: For development purposes, authentication is currently disabled. In production, implement proper JWT authentication.

## Development

### Project Structure

```java
src/
├── main/
│   ├── java/com/edulearnorg/ltt/smeplanner/
│   │   ├── SmePlannerBackendApplication.java
│   │   ├── config/
│   │   │   └── SecurityConfig.java
│   │   ├── controller/
│   │   │   ├── ActivityController.java
│   │   │   ├── ScheduleController.java
│   │   │   └── SMEController.java
│   │   ├── dto/
│   │   │   ├── AvailableTimeSlots.java
│   │   │   ├── ErrorResponse.java
│   │   │   ├── ScheduleCreateRequest.java
│   │   │   ├── SMEAvailability.java
│   │   │   └── TimeSlot.java
│   │   ├── entity/
│   │   │   ├── Activity.java
│   │   │   ├── Schedule.java
│   │   │   └── SME.java
│   │   ├── repository/
│   │   │   ├── ActivityRepository.java
│   │   │   ├── ScheduleRepository.java
│   │   │   └── SMERepository.java
│   │   └── service/
│   │       ├── ActivityService.java
│   │       └── ScheduleService.java
│   └── resources/
│       ├── application.properties
│       └── data.sql
```

### Building

```bash
mvn clean compile
```

### Testing

```bash
mvn test
```

### Packaging

```bash
mvn clean package
```

## Contributing

1. Follow the existing code style and patterns
2. Use constructor injection for dependencies
3. Add appropriate validation and error handling
4. Update documentation for any API changes

## License

This project is proprietary to EduLearnOrg L&TT team.
