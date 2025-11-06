# Quarkus Kotlin Multi-Module Template

A production-ready microservice template built with **Kotlin**, **Quarkus**, and **Hexagonal Architecture** (Ports & Adapters pattern) using a **multi-module Gradle** approach.

## Architecture Overview

This template follows the **Hexagonal Architecture** pattern with a fine-grained module structure:

```
ms-quarkus-kt-template/
├── domain/              # Core business logic (no external dependencies)
├── application/         # Use cases and business orchestration
├── rest-adapter/        # REST API input adapter
├── persistence-adapter/ # Database output adapter
└── bootstrap/           # Main application (dependency wiring)
```

### Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                        REST Adapter                          │
│              (Input Port - HTTP/REST API)                    │
│  - UserResource (REST endpoints)                             │
│  - Request/Response DTOs                                     │
│  - Exception Handlers                                        │
└────────────────┬────────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────────┐
│                    Application Layer                         │
│              (Use Cases - Business Logic)                    │
│  - CreateUserUseCase                                         │
│  - GetUserUseCase                                            │
│  - UpdateUserUseCase                                         │
│  - DeleteUserUseCase                                         │
└────────────────┬────────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────────┐
│                       Domain Layer                           │
│         (Core Business Logic - Framework Free)               │
│  - User (Aggregate)                                          │
│  - Value Objects (UserId, Email, UserName)                   │
│  - UserRepository (Port Interface)                           │
│  - Domain Exceptions                                         │
└─────────────────────────────────────────────────────────────┘
                 ▲
                 │
┌────────────────┴────────────────────────────────────────────┐
│                   Persistence Adapter                        │
│          (Output Port - Database Access)                     │
│  - UserEntity (JPA Entity)                                   │
│  - UserRepositoryImpl                                        │
│  - Flyway Migrations                                         │
└─────────────────────────────────────────────────────────────┘
```

## Features

### Core Features
- **Hexagonal Architecture** with clear separation of concerns
- **Multi-module Gradle** setup with Kotlin DSL
- **Domain-Driven Design** with value objects and aggregates
- **CQRS-style** command/query DTOs

### Technical Features
- ✅ **Quarkus 3.29.0** - Supersonic Subatomic Java
- ✅ **Kotlin 2.1.0** - Modern JVM language
- ✅ **PostgreSQL** with Hibernate Panache Kotlin
- ✅ **Flyway** - Database migrations
- ✅ **OpenAPI/Swagger** - API documentation
- ✅ **Bean Validation** - Request validation
- ✅ **JWT/OIDC** - Security and authentication
- ✅ **Health Checks** - Liveness and readiness probes
- ✅ **Metrics** - Prometheus metrics export
- ✅ **OpenTelemetry** - Distributed tracing
- ✅ **Docker & Docker Compose** - Containerization
- ✅ **Integration Tests** - REST Assured tests

## Module Structure

### 1. Domain Module
**Pure business logic with no external dependencies**

- `model/` - Domain entities and value objects
- `port/` - Repository interfaces (output ports)
- `exception/` - Domain-specific exceptions

### 2. Application Module
**Use cases and business logic orchestration**

- `usecase/` - Application use cases
- `dto/` - Command and query DTOs

### 3. REST Adapter Module
**Input adapter for HTTP/REST API**

- `resource/` - JAX-RS REST endpoints
- `dto/` - Request/Response DTOs
- `exception/` - Exception mappers

### 4. Persistence Adapter Module
**Output adapter for database access**

- `entity/` - JPA entities
- `repository/` - Repository implementations
- `db/migration/` - Flyway SQL scripts

### 5. Bootstrap Module
**Main application and dependency wiring**

- Application configuration
- CDI dependency injection setup
- Integration tests

## Prerequisites

- **Java 21** or higher
- **Gradle 8.x** (wrapper included)
- **Docker & Docker Compose** (for running PostgreSQL)

## Getting Started

### 1. Clone the Repository

```bash
git clone <your-repo-url>
cd ms-quarkus-kt-template
```

### 2. Run with Dev Services (Recommended for Development)

Quarkus Dev Services will automatically start a PostgreSQL container:

```bash
./gradlew :bootstrap:quarkusDev
```

The application will start on `http://localhost:8080`

### 3. Run with Docker Compose

```bash
# Start PostgreSQL
docker-compose up -d postgres

# Run the application
./gradlew :bootstrap:quarkusDev

# Or build and run everything with Docker
docker-compose up --build
```

### 4. Access the Application

- **API Base URL**: http://localhost:8080/api/v1
- **Swagger UI**: http://localhost:8080/swagger-ui
- **Health Check**: http://localhost:8080/health
- **Metrics**: http://localhost:8080/metrics

## API Endpoints

### User Management

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/v1/users` | Create a new user | No |
| GET | `/api/v1/users` | Get all users | Yes (user/admin) |
| GET | `/api/v1/users/{id}` | Get user by ID | Yes (user/admin) |
| PUT | `/api/v1/users/{id}` | Update user | Yes (user/admin) |
| DELETE | `/api/v1/users/{id}` | Delete user | Yes (admin only) |

### Example Request

```bash
# Create a user
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "firstName": "John",
    "lastName": "Doe"
  }'

# Get all users
curl http://localhost:8080/api/v1/users

# Get user by ID
curl http://localhost:8080/api/v1/users/{userId}
```

## Development Commands

### Build the Project

```bash
# Build all modules
./gradlew build

# Build without tests
./gradlew build -x test
```

### Run Tests

```bash
# Run all tests
./gradlew test

# Run tests for a specific module
./gradlew :domain:test
./gradlew :bootstrap:test
```

### Run in Development Mode

```bash
# With live reload
./gradlew :bootstrap:quarkusDev
```

### Build Docker Image

```bash
# Build with Gradle
./gradlew build

# Build Docker image
docker build -t quarkus-kotlin-template .
```

### Database Migrations

```bash
# Flyway migrations run automatically on startup
# Migration files are in: persistence-adapter/src/main/resources/db/migration/
```

## Configuration

### Application Properties

Configuration file: `bootstrap/src/main/resources/application.properties`

Key configurations:
- Database connection
- Security (OIDC/JWT)
- Health checks
- Metrics and tracing
- Logging

### Environment Variables

Override properties using environment variables:

```bash
QUARKUS_DATASOURCE_JDBC_URL=jdbc:postgresql://localhost:5432/mydb
QUARKUS_DATASOURCE_USERNAME=myuser
QUARKUS_DATASOURCE_PASSWORD=mypassword
```

## Monitoring

### With Docker Compose

Start monitoring stack (Prometheus + Grafana):

```bash
docker-compose --profile monitoring up -d
```

- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)

### Metrics

View metrics at: http://localhost:8080/metrics

Available metrics:
- HTTP request metrics
- JVM metrics
- Database connection pool metrics
- Custom business metrics

## Security

### Authentication

The template is configured for OIDC/JWT authentication.

**For development**, security is disabled by default. Enable it by:

1. Configure OIDC server in `application.properties`:
   ```properties
   quarkus.oidc.auth-server-url=https://your-auth-server/realms/your-realm
   quarkus.oidc.client-id=your-client-id
   quarkus.oidc.credentials.secret=your-secret
   ```

2. Enable security:
   ```properties
   quarkus.oidc.enabled=true
   ```

### Authorization

Endpoints are annotated with:
- `@PermitAll` - No authentication required
- `@RolesAllowed("user", "admin")` - Requires specific roles

## Testing

### Unit Tests

Domain and application modules have unit tests with no external dependencies.

### Integration Tests

Bootstrap module contains full integration tests using:
- **Quarkus Test** framework
- **REST Assured** for API testing
- **H2** in-memory database

Run integration tests:
```bash
./gradlew :bootstrap:test
```

## Project Structure Details

```
ms-quarkus-kt-template/
├── build.gradle.kts              # Root build configuration
├── settings.gradle.kts           # Module declarations
├── gradle.properties             # Gradle properties
├── Dockerfile                    # Docker image build
├── docker-compose.yml            # Docker Compose setup
├── prometheus.yml                # Prometheus configuration
│
├── domain/                       # Domain module (pure Kotlin)
│   ├── build.gradle.kts
│   └── src/main/kotlin/com/example/template/domain/
│       ├── model/               # Entities and value objects
│       ├── port/                # Repository interfaces
│       └── exception/           # Domain exceptions
│
├── application/                  # Application module
│   ├── build.gradle.kts
│   └── src/main/kotlin/com/example/template/application/
│       ├── usecase/             # Use case implementations
│       └── dto/                 # Command/Query DTOs
│
├── rest-adapter/                 # REST adapter module
│   ├── build.gradle.kts
│   └── src/main/kotlin/com/example/template/adapter/rest/
│       ├── resource/            # JAX-RS resources
│       ├── dto/                 # REST DTOs
│       └── exception/           # Exception mappers
│
├── persistence-adapter/          # Persistence adapter module
│   ├── build.gradle.kts
│   └── src/main/
│       ├── kotlin/com/example/template/adapter/persistence/
│       │   ├── entity/          # JPA entities
│       │   └── repository/      # Repository implementations
│       └── resources/db/migration/  # Flyway migrations
│
└── bootstrap/                    # Bootstrap module
    ├── build.gradle.kts
    └── src/
        ├── main/resources/
        │   └── application.properties
        └── test/kotlin/com/example/template/
            └── UserResourceTest.kt
```

## Customization Guide

### Adding a New Entity

1. **Domain Module**: Create entity, value objects, and repository port
2. **Application Module**: Create use cases and DTOs
3. **REST Adapter**: Create REST endpoints and DTOs
4. **Persistence Adapter**: Create JPA entity, repository implementation, and migration
5. **Bootstrap**: Write integration tests

### Adding New Adapters

Create new adapter modules following the same pattern:
- `messaging-adapter` - For Kafka/RabbitMQ
- `cache-adapter` - For Redis/Caffeine
- `external-api-adapter` - For external API integrations

## Best Practices

1. **Keep domain pure** - No framework dependencies in domain module
2. **Use value objects** - Encapsulate validation and business rules
3. **Test coverage** - Aim for high coverage especially in domain/application
4. **DTOs for boundaries** - Convert at adapter boundaries
5. **Immutability** - Use Kotlin data classes and immutable collections
6. **Fail fast** - Validate at boundaries (REST DTOs, domain objects)

## Troubleshooting

### Port Already in Use

```bash
# Change port in application.properties
quarkus.http.port=8081
```

### Database Connection Issues

```bash
# Check PostgreSQL is running
docker-compose ps

# View PostgreSQL logs
docker-compose logs postgres
```

### Build Fails

```bash
# Clean and rebuild
./gradlew clean build --refresh-dependencies
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the Apache License 2.0.

## Resources

- [Quarkus Documentation](https://quarkus.io/guides/)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/)
- [Gradle Kotlin DSL](https://docs.gradle.org/current/userguide/kotlin_dsl.html)

---

**Happy Coding!** 🚀
