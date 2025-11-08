# Quarkus Kotlin Multi-Module Template

A production-ready microservice template built with **Kotlin**, **Quarkus**, and **Hexagonal Architecture** (Ports & Adapters pattern) using a **multi-module Gradle** approach.

[![Hexagonal Architecture](https://img.shields.io/badge/Architecture-Hexagonal-blue)](https://alistair.cockburn.us/hexagonal-architecture/)
[![Quarkus](https://img.shields.io/badge/Quarkus-3.29.1-red)](https://quarkus.io/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.21-purple)](https://kotlinlang.org/)

## Table of Contents

- [Architecture Overview](#architecture-overview)
- [Why Hexagonal Architecture?](#why-hexagonal-architecture)
- [Tech Stack & Design Decisions](#tech-stack--design-decisions)
- [Module Structure](#module-structure)
- [How to Implement Business Logic](#how-to-implement-business-logic)
- [How to Add More Adapters](#how-to-add-more-adapters)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Configuration](#configuration)
- [Testing Strategy](#testing-strategy)
- [Observability](#observability)
- [Troubleshooting](#troubleshooting)

---

## Architecture Overview

This template implements **Hexagonal Architecture** (also known as Ports and Adapters pattern), which ensures:

- **Business logic independence** from frameworks and infrastructure
- **Easy testability** through dependency inversion
- **Flexibility** to swap or add adapters without changing core logic
- **Clear boundaries** between layers

### Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                   INPUT ADAPTERS (Driving)                      │
│  ┌──────────────────────┐      ┌──────────────────────┐         │
│  │   REST Adapter       │      │  GraphQL Adapter     │         │
│  │  (adapter-input-rest)│      │   (future)           │         │
│  │ • UserResource       │      │                      │         │
│  │ • Exception Mappers  │      │                      │         │
│  │ • Request/Response   │      │                      │         │
│  └──────────┬───────────┘      └──────────┬───────────┘         │
└─────────────┼─────────────────────────────┼─────────────────────┘
              │                             │
              ▼                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                    APPLICATION LAYER                            │
│              (Use Cases - Business Orchestration)               │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ • CreateUserUseCase    • GetUserUseCase                 │    │
│  │ • GetAllUsersUseCase   • UpdateUserUseCase              │    │
│  │ • DeleteUserUseCase                                     │    │
│  │                                                         │    │
│  │ Commands & Responses:                                   │    │
│  │ • CreateUserCommand    • UserResponse                   │    │
│  │ • UpdateUserCommand                                     │    │
│  └─────────────────────────────────────────────────────────┘    │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                      DOMAIN LAYER                               │
│           (Core Business Logic - Framework Free)                │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ Aggregates:                                             │    │
│  │ • User (id, email, name, createdAt, updatedAt)          │    │
│  │   - create(), update(), validate()                      │    │
│  │                                                         │    │
│  │ Value Objects:                                          │    │
│  │ • UserId(UUID)        - Identity                        │    │
│  │ • Email(String)       - Validation                      │    │
│  │ • UserName(String)    - Business rules                  │    │
│  │                                                         │    │
│  │ Ports (Interfaces):                                     │    │
│  │ • UserRepository (output port)                          │    │
│  │   - save(), findById(), findByEmail(), etc.             │    │
│  │                                                         │    │
│  │ Domain Exceptions:                                      │    │
│  │ • UserNotFoundException                                 │    │
│  │ • UserAlreadyExistsException                            │    │
│  │ • DomainException (base)                                │    │
│  └─────────────────────────────────────────────────────────┘    │
└────────────────────────────┬────────────────────────────────────┘
                             ▲
                             │
┌────────────────────────────┴────────────────────────────────────┐
│                OUTPUT ADAPTERS (Driven)                         │
│  ┌──────────────────────┐      ┌──────────────────────┐         │
│  │ Persistence Adapter  │      │   Redis Adapter      │         │
│  │ (adapter-output-     │      │    (future)          │         │
│  │     persistence)     │      │                      │         │
│  │ • UserRepositoryImpl │      │                      │         │
│  │ • UserEntity (JPA)   │      │                      │         │
│  │ • Flyway Migrations  │      │                      │         │
│  └──────────────────────┘      └──────────────────────┘         │
│                 PostgreSQL                Redis                 │
└─────────────────────────────────────────────────────────────────┘
```

### Dependency Flow (The Key Rule)

```
Domain → NOTHING                                [ZERO DEPENDENCIES]
   ↑
Application → domain                            [USE CASES]
   ↑
Input Adapters → application + domain           [REST, GraphQL, etc.]
Output Adapters → domain                        [Database, Cache, etc.]
   ↑
Bootstrap → ALL modules                         [WIRING]
```

**Key Principle**: Dependencies point **inward** toward the domain. The domain never knows about frameworks or infrastructure.

---

## Why Hexagonal Architecture?

### The Problem with Traditional Layered Architecture

Traditional N-tier architecture often suffers from:
- **Framework coupling**: Business logic mixed with framework code
- **Hard to test**: Requires databases/external services to test
- **Hard to change**: Replacing a database or API framework requires rewriting business logic
- **Unclear boundaries**: Business logic leaks into controllers or repositories

### The Hexagonal Solution

Hexagonal Architecture solves these by:

1. **Isolating Business Logic**: Domain layer has zero framework dependencies
2. **Dependency Inversion**: Core depends on abstractions (ports), not implementations (adapters)
3. **Testability**: Test business logic with mocks, no infrastructure needed
4. **Flexibility**: Swap adapters (REST → GraphQL, PostgreSQL → MongoDB) without touching core logic
5. **Clear Contracts**: Ports define clear interfaces between layers

### Real-World Benefits

- **Swap databases easily**: PostgreSQL → MongoDB requires only changing the persistence adapter
- **Multiple input channels**: Same business logic serves REST API, GraphQL, CLI, message queues
- **Technology upgrades**: Framework updates don't affect business logic
- **Team autonomy**: Frontend, backend, and infrastructure teams work independently
- **Test in isolation**: Domain tests run in milliseconds without infrastructure

---

## Tech Stack & Design Decisions

### Core Technologies

| Technology | Version | Why We Chose It |
|------------|---------|-----------------|
| **Kotlin** | 2.2.21 | • Null safety prevents NPEs<br>• Data classes reduce boilerplate<br>• Coroutines for async (future)<br>• Excellent Java interop |
| **Quarkus** | 3.29.1 | • Fastest startup time (0.05s)<br>• Low memory footprint (70MB)<br>• Native compilation support<br>• Dev mode with live reload<br>• Built for cloud-native/Kubernetes |
| **Gradle** | 8.12 | • Multi-module project support<br>• Kotlin DSL for type-safe builds<br>• Better caching than Maven<br>• Parallel execution |
| **PostgreSQL** | 16 | • ACID compliance<br>• JSON support for flexibility<br>• Excellent performance<br>• Production-proven |

### Framework Choices

#### Why Quarkus over Spring Boot?

| Feature | Quarkus | Spring Boot |
|---------|---------|-------------|
| Startup time | 0.05s | 3-5s |
| Memory usage | 70MB | 150-200MB |
| Native compilation | Yes (GraalVM) | Limited |
| Reactive | First-class | Add-on |
| Dev mode | Live reload | Requires restart |
| Kubernetes | Built-in | External tools |

**Decision**: Quarkus for cloud-native, containerized microservices.

#### Why Hibernate Panache over Spring Data?

- **Less boilerplate**: No `@Repository` interfaces for simple queries
- **Active Record pattern**: `User.findById()` instead of `userRepository.findById()`
- **Quarkus-optimized**: Better integration with Quarkus
- **Kotlin support**: First-class Kotlin extensions

#### Why Flyway over Liquibase?

- **Simplicity**: Plain SQL migrations (no XML/YAML)
- **Version control friendly**: Easy to review in PRs
- **Predictable**: Runs in order, no surprises
- **Lightweight**: No additional dependencies

### Architecture Patterns

#### 1. Value Objects

**Why**: Encapsulate validation and business rules at the type level.

```kotlin
// Instead of primitives:
fun createUser(email: String, name: String) // Can pass invalid data

// We use value objects:
fun createUser(email: Email, name: UserName) // Type-safe, validated
```

**Benefits**:
- Compile-time safety
- Self-documenting code
- Validation in one place
- Immutable by default

#### 2. Use Case Pattern

**Why**: Each use case is a single, testable business operation.

```kotlin
class CreateUserUseCase(private val userRepository: UserRepository) {
    operator fun invoke(command: CreateUserCommand): UserResponse {
        // Single responsibility: create a user
        val email = Email(command.email)
        val name = UserName(command.firstName, command.lastName)
        val user = User.create(email, name)

        if (userRepository.existsByEmail(email)) {
            throw UserAlreadyExistsException(email.value)
        }

        val savedUser = userRepository.save(user)
        return UserResponse.fromDomain(savedUser)
    }
}
```

**Benefits**:
- Clear intent (what does this do?)
- Easy to test (mock repository)
- Easy to maintain (one class, one job)
- Reusable across adapters

#### 3. Command-Response Pattern

**Why**: Clear contracts between layers, decoupled from HTTP.

```kotlin
// Command (input)
data class CreateUserCommand(
    val email: String,
    val firstName: String,
    val lastName: String
)

// Response (output)
data class UserResponse(
    val id: String,
    val email: String,
    val fullName: String,
    val createdAt: Instant
)
```

**Benefits**:
- Decouple from HTTP requests/responses
- Reuse same DTOs for REST, GraphQL, messaging
- Easy to version (CreateUserCommandV2)

#### 4. Exception Mapping

**Why**: Convert domain exceptions to HTTP status codes at the boundary.

```kotlin
// Domain layer
throw UserNotFoundException(userId)

// Adapter layer maps to HTTP
@ServerExceptionMapper
fun mapUserNotFound(ex: UserNotFoundException): Response {
    return Response.status(404).entity(ErrorResponse(...)).build()
}
```

**Benefits**:
- Domain doesn't know about HTTP
- Same exception handling for all adapters
- Clear error responses

---

## Module Structure

### 1. Domain Module (`/domain`)

**Purpose**: Pure business logic, zero external dependencies.

**Contents**:
```
domain/src/main/kotlin/.../domain/
├── model/
│   ├── User.kt              # Aggregate root
│   ├── Email.kt             # Value object
│   ├── UserId.kt            # Value object
│   └── UserName.kt          # Value object
├── port/
│   └── output/
│       └── UserRepository.kt # Output port interface
└── exception/
    ├── DomainException.kt
    ├── UserNotFoundException.kt
    └── UserAlreadyExistsException.kt
```

**Key Files**:

- **User.kt** (domain/src/main/kotlin/.../domain/model/User.kt:11-45)
  ```kotlin
  data class User(
      val id: UserId,
      val email: Email,
      val name: UserName,
      val createdAt: Instant,
      val updatedAt: Instant
  ) {
      companion object {
          fun create(email: Email, name: UserName): User {
              // Business logic for creation
          }
      }

      fun update(name: UserName): User {
          // Business logic for updates
      }
  }
  ```

- **Email.kt** (domain/src/main/kotlin/.../domain/model/Email.kt:7-18)
  ```kotlin
  @JvmInline
  value class Email(val value: String) {
      init {
          require(value.matches(EMAIL_REGEX)) {
              "Invalid email format: $value"
          }
      }
  }
  ```

**Dependencies**:
- `jakarta.inject:jakarta.inject-api` (API only, for annotations)
- **No framework dependencies**

**Rules**:
- ✅ Can contain: Business logic, validation, domain models
- ❌ Cannot contain: Annotations like @Entity, @RestController, database code
- ✅ Can depend on: Nothing (pure Kotlin/Java)
- ❌ Cannot depend on: Quarkus, JPA, Jackson, etc.

---

### 2. Application Module (`/application`)

**Purpose**: Business orchestration and use cases.

**Contents**:
```
application/src/main/kotlin/.../application/
├── usecase/
│   ├── CreateUserUseCase.kt
│   ├── GetUserUseCase.kt
│   ├── GetAllUsersUseCase.kt
│   ├── UpdateUserUseCase.kt
│   └── DeleteUserUseCase.kt
└── dto/
    ├── CreateUserCommand.kt
    ├── UpdateUserCommand.kt
    └── UserResponse.kt
```

**Key Pattern**: Use Cases

Each use case follows this structure:
```kotlin
@ApplicationScoped
class CreateUserUseCase @Inject constructor(
    private val userRepository: UserRepository // Depends on PORT, not implementation
) {
    operator fun invoke(command: CreateUserCommand): UserResponse {
        // 1. Convert command to domain model
        val email = Email(command.email)
        val name = UserName(command.firstName, command.lastName)

        // 2. Business validation
        if (userRepository.existsByEmail(email)) {
            throw UserAlreadyExistsException(email.value)
        }

        // 3. Business logic
        val user = User.create(email, name)

        // 4. Persist
        val savedUser = userRepository.save(user)

        // 5. Return response
        return UserResponse.fromDomain(savedUser)
    }
}
```

**Dependencies**:
- `domain` module
- `quarkus-arc` (compile-only, for CDI)

**Rules**:
- ✅ Can orchestrate: Multiple domain operations
- ✅ Can depend on: Domain ports (interfaces)
- ❌ Cannot depend on: Adapters (implementations)
- ✅ Transaction boundaries: Use cases define transaction boundaries

---

### 3. Input Adapters

#### REST Adapter (`/adapter-input-rest`)

**Purpose**: HTTP/REST API exposure.

**Contents**:
```
adapter-input-rest/src/main/kotlin/.../adapter/rest/
├── resource/
│   └── UserResource.kt          # REST endpoints
├── dto/
│   ├── CreateUserRequest.kt     # HTTP request DTO
│   ├── UpdateUserRequest.kt
│   └── ErrorResponse.kt
└── exception/
    └── GlobalExceptionHandler.kt # Exception mapping
```

**Key File**: UserResource.kt (adapter-input-rest/src/main/kotlin/.../UserResource.kt:39-157)

```kotlin
@Path("/api/v1/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class UserResource @Inject constructor(
    private val createUserUseCase: CreateUserUseCase,
    private val getUserUseCase: GetUserUseCase,
    // ... other use cases
) {
    @POST
    @PermitAll
    fun createUser(@Valid request: CreateUserRequest): Response {
        // 1. Convert HTTP request to Command
        val command = request.toCommand()

        // 2. Call use case
        val response = createUserUseCase(command)

        // 3. Return HTTP response
        return Response.status(Response.Status.CREATED).entity(response).build()
    }

    @GET
    @Path("/{id}")
    @RolesAllowed("user", "admin")
    fun getUser(@PathParam("id") id: String): Response {
        val userId = UserId(UUID.fromString(id))
        val response = getUserUseCase(userId)
        return Response.ok(response).build()
    }
}
```

**Exception Mapping**: GlobalExceptionHandler.kt

```kotlin
@ServerExceptionMapper
fun handleUserNotFound(exception: UserNotFoundException): Response {
    return Response
        .status(Response.Status.NOT_FOUND)
        .entity(ErrorResponse(
            message = exception.message ?: "User not found",
            timestamp = Instant.now()
        ))
        .build()
}

@ServerExceptionMapper
fun handleUserAlreadyExists(exception: UserAlreadyExistsException): Response {
    return Response
        .status(Response.Status.CONFLICT)
        .entity(ErrorResponse(...))
        .build()
}
```

**Dependencies**:
- `application` module
- `domain` module
- `quarkus-rest-jackson`
- `quarkus-hibernate-validator`
- `quarkus-oidc`
- `quarkus-smallrye-openapi`

**Rules**:
- ✅ Responsibilities: HTTP concerns, serialization, validation
- ✅ Convert: HTTP DTOs → Commands → Use Cases → Responses → HTTP DTOs
- ❌ Cannot contain: Business logic (delegate to use cases)

---

### 4. Output Adapters

#### Persistence Adapter (`/adapter-output-persistence`)

**Purpose**: Database access implementation.

**Contents**:
```
adapter-output-persistence/src/main/
├── kotlin/.../adapter/persistence/
│   ├── repository/
│   │   └── UserRepositoryImpl.kt    # Implements UserRepository port
│   └── entity/
│       └── UserEntity.kt            # JPA entity
└── resources/
    └── db/migration/
        └── V1__create_users_table.sql
```

**Key Pattern**: Port Implementation

**UserRepositoryImpl.kt** (adapter-output-persistence/src/main/kotlin/.../UserRepositoryImpl.kt:16-47)

```kotlin
@ApplicationScoped
class UserRepositoryImpl @Inject constructor(
    private val entityManager: EntityManager
) : UserRepository {  // Implements domain port

    override fun save(user: User): User {
        val entity = UserEntity.fromDomain(user)  // Domain → Entity
        entityManager.persist(entity)
        return entity.toDomain()  // Entity → Domain
    }

    override fun findById(id: UserId): User? {
        return entityManager.find(UserEntity::class.java, id.value)
            ?.toDomain()
    }

    override fun findByEmail(email: Email): User? {
        return entityManager
            .createQuery("SELECT u FROM UserEntity u WHERE u.email = :email", UserEntity::class.java)
            .setParameter("email", email.value)
            .resultList
            .firstOrNull()
            ?.toDomain()
    }

    // ... other methods
}
```

**Key Pattern**: Entity Conversion

**UserEntity.kt** (adapter-output-persistence/src/main/kotlin/.../UserEntity.kt:53-80)

```kotlin
@Entity
@Table(name = "users")
class UserEntity {
    @Id
    lateinit var id: UUID

    @Column(nullable = false, unique = true)
    lateinit var email: String

    // ... other fields

    companion object {
        fun fromDomain(user: User): UserEntity {
            // Domain → Entity conversion
            return UserEntity().apply {
                id = user.id.value
                email = user.email.value
                // ...
            }
        }
    }

    fun toDomain(): User {
        // Entity → Domain conversion
        return User(
            id = UserId(id),
            email = Email(email),
            // ...
        )
    }
}
```

**Dependencies**:
- `domain` module (NOT application!)
- `quarkus-hibernate-orm-panache-kotlin`
- `quarkus-jdbc-postgresql`
- `quarkus-flyway`

**Rules**:
- ✅ Implements: Domain repository ports
- ✅ Returns: Domain models (never entities)
- ❌ Cannot: Call use cases or application layer
- ✅ Transactions: Managed by Quarkus (@Transactional on use cases)

---

### 5. Bootstrap Module (`/bootstrap`)

**Purpose**: Application entry point and dependency wiring.

**Contents**:
```
bootstrap/src/
├── main/
│   ├── kotlin/.../bootstrap/
│   │   └── Application.kt       # Main entry point
│   └── resources/
│       ├── application.properties
│       └── META-INF/
│           └── beans.xml        # CDI configuration
└── test/
    └── kotlin/.../
        ├── UserResourceTest.kt  # Integration tests
        └── architecture/
            └── ArchitectureTest.kt # Architecture rules tests
```

**Dependencies**: ALL modules
- `domain`
- `application`
- `adapter-input-rest`
- `adapter-output-persistence`
- All Quarkus dependencies

**Rules**:
- ✅ Wires: All dependencies via CDI
- ✅ Contains: Configuration, integration tests
- ❌ Cannot: Contain business logic

---

## How to Implement Business Logic

Let's walk through adding a new feature: **"Deactivate User"**

### Step 1: Domain Layer

First, add business logic to the domain.

**1.1 Add method to User.kt**

```kotlin
// domain/src/main/kotlin/.../domain/model/User.kt
data class User(
    val id: UserId,
    val email: Email,
    val name: UserName,
    val isActive: Boolean = true,  // Add this field
    val createdAt: Instant,
    val updatedAt: Instant
) {
    fun deactivate(): User {
        require(isActive) { "User is already deactivated" }
        return copy(
            isActive = false,
            updatedAt = Instant.now()
        )
    }
}
```

**1.2 Add exception**

```kotlin
// domain/src/main/kotlin/.../domain/exception/UserAlreadyDeactivatedException.kt
class UserAlreadyDeactivatedException(userId: String)
    : DomainException("User $userId is already deactivated")
```

### Step 2: Application Layer

Create a use case to orchestrate the business logic.

**2.1 Create use case**

```kotlin
// application/src/main/kotlin/.../application/usecase/DeactivateUserUseCase.kt
@ApplicationScoped
class DeactivateUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(userId: UserId): UserResponse {
        // 1. Load user
        val user = userRepository.findById(userId)
            ?: throw UserNotFoundException(userId.value.toString())

        // 2. Business logic (domain method)
        val deactivatedUser = user.deactivate()

        // 3. Persist
        val savedUser = userRepository.save(deactivatedUser)

        // 4. Return response
        return UserResponse.fromDomain(savedUser)
    }
}
```

### Step 3: Input Adapter (REST)

Expose the use case via HTTP endpoint.

**3.1 Add endpoint to UserResource.kt**

```kotlin
// adapter-input-rest/src/main/kotlin/.../resource/UserResource.kt
@PUT
@Path("/{id}/deactivate")
@RolesAllowed("admin")
fun deactivateUser(@PathParam("id") id: String): Response {
    val userId = UserId(UUID.fromString(id))
    val response = deactivateUserUseCase(userId)
    return Response.ok(response).build()
}
```

**3.2 Add exception mapper**

```kotlin
// adapter-input-rest/src/main/kotlin/.../exception/GlobalExceptionHandler.kt
@ServerExceptionMapper
fun handleUserAlreadyDeactivated(exception: UserAlreadyDeactivatedException): Response {
    return Response
        .status(Response.Status.CONFLICT)
        .entity(ErrorResponse(
            message = exception.message ?: "User already deactivated",
            timestamp = Instant.now()
        ))
        .build()
}
```

### Step 4: Output Adapter (Persistence)

Update database schema and entity mapping.

**4.1 Create migration**

```sql
-- adapter-output-persistence/src/main/resources/db/migration/V2__add_user_active_flag.sql
ALTER TABLE users ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT true;
CREATE INDEX idx_users_active ON users(is_active);
```

**4.2 Update UserEntity.kt**

```kotlin
// adapter-output-persistence/src/main/kotlin/.../entity/UserEntity.kt
@Entity
@Table(name = "users")
class UserEntity {
    // ... existing fields

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true

    companion object {
        fun fromDomain(user: User): UserEntity {
            return UserEntity().apply {
                // ... existing mappings
                isActive = user.isActive
            }
        }
    }

    fun toDomain(): User {
        return User(
            // ... existing mappings
            isActive = isActive
        )
    }
}
```

### Step 5: Test

Write tests at each layer.

**5.1 Domain test**

```kotlin
// domain/src/test/kotlin/.../domain/model/UserTest.kt
class UserTest {
    @Test
    fun `should deactivate active user`() {
        val user = User.create(Email("test@example.com"), UserName("John", "Doe"))
        val deactivated = user.deactivate()

        assertFalse(deactivated.isActive)
    }

    @Test
    fun `should throw exception when deactivating inactive user`() {
        val user = User(..., isActive = false)

        assertThrows<IllegalArgumentException> {
            user.deactivate()
        }
    }
}
```

**5.2 Integration test**

```kotlin
// bootstrap/src/test/kotlin/.../UserResourceTest.kt
@QuarkusTest
class UserResourceTest {
    @Test
    fun `should deactivate user`() {
        // Create user
        val createResponse = given()
            .contentType(ContentType.JSON)
            .body(CreateUserRequest(...))
        .`when`()
            .post("/api/v1/users")
        .then()
            .statusCode(201)
            .extract().`as`(UserResponse::class.java)

        // Deactivate
        given()
        .`when`()
            .put("/api/v1/users/${createResponse.id}/deactivate")
        .then()
            .statusCode(200)
            .body("isActive", equalTo(false))
    }
}
```

### Complete! ✅

You've now implemented a complete feature following hexagonal architecture:

- **Domain**: Business logic (deactivate method)
- **Application**: Use case orchestration
- **Input Adapter**: REST endpoint
- **Output Adapter**: Database persistence
- **Tests**: Unit and integration tests

---

## How to Add More Adapters

Hexagonal architecture makes it easy to add new adapters without changing core logic.

### Example 1: Add GraphQL Adapter

**1. Create new module**

```bash
mkdir adapter-input-graphql
```

**2. Add build.gradle.kts**

```kotlin
// adapter-input-graphql/build.gradle.kts
dependencies {
    implementation(project(":domain"))
    implementation(project(":application"))
    implementation("io.quarkus:quarkus-smallrye-graphql")
}
```

**3. Create GraphQL resource**

```kotlin
// adapter-input-graphql/src/main/kotlin/.../UserGraphQLResource.kt
@GraphQLApi
class UserGraphQLResource @Inject constructor(
    private val createUserUseCase: CreateUserUseCase,
    private val getUserUseCase: GetUserUseCase
) {
    @Mutation
    fun createUser(email: String, firstName: String, lastName: String): UserResponse {
        val command = CreateUserCommand(email, firstName, lastName)
        return createUserUseCase(command)  // Same use case!
    }

    @Query
    fun user(id: String): UserResponse {
        return getUserUseCase(UserId(UUID.fromString(id)))
    }
}
```

**4. Register in settings.gradle.kts**

```kotlin
include("adapter-input-graphql")
```

**5. Add to bootstrap dependencies**

```kotlin
// bootstrap/build.gradle.kts
dependencies {
    implementation(project(":adapter-input-graphql"))
}
```

**Done!** Now you have both REST and GraphQL serving the same business logic.

---

### Example 2: Add Redis Cache Adapter

**1. Create output adapter**

```bash
mkdir adapter-output-cache
```

**2. Define port in domain**

```kotlin
// domain/src/main/kotlin/.../domain/port/output/UserCache.kt
interface UserCache {
    fun get(userId: UserId): User?
    fun put(user: User)
    fun evict(userId: UserId)
}
```

**3. Implement adapter**

```kotlin
// adapter-output-cache/src/main/kotlin/.../RedisCacheImpl.kt
@ApplicationScoped
class RedisCacheImpl @Inject constructor(
    private val redisClient: RedisClient
) : UserCache {

    override fun get(userId: UserId): User? {
        val json = redisClient.get(userId.value.toString())
        return json?.let { objectMapper.readValue(it, User::class.java) }
    }

    override fun put(user: User) {
        val json = objectMapper.writeValueAsString(user)
        redisClient.setex(user.id.value.toString(), 3600, json)
    }

    override fun evict(userId: UserId) {
        redisClient.del(userId.value.toString())
    }
}
```

**4. Update use case**

```kotlin
// application/src/main/kotlin/.../application/usecase/GetUserUseCase.kt
@ApplicationScoped
class GetUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val userCache: UserCache  // Inject cache
) {
    operator fun invoke(userId: UserId): UserResponse {
        // Check cache first
        val cachedUser = userCache.get(userId)
        if (cachedUser != null) {
            return UserResponse.fromDomain(cachedUser)
        }

        // Load from database
        val user = userRepository.findById(userId)
            ?: throw UserNotFoundException(userId.value.toString())

        // Store in cache
        userCache.put(user)

        return UserResponse.fromDomain(user)
    }
}
```

**Benefits**:
- ✅ Cache logic isolated in adapter
- ✅ Easy to swap Redis for Caffeine/Hazelcast
- ✅ Domain and use cases remain clean
- ✅ Can disable cache with CDI alternatives

---

### Example 3: Add Kafka Event Publisher

**1. Define port**

```kotlin
// domain/src/main/kotlin/.../domain/port/output/EventPublisher.kt
interface EventPublisher {
    fun publish(event: DomainEvent)
}

sealed class DomainEvent {
    data class UserCreated(val userId: UserId, val email: Email) : DomainEvent()
    data class UserDeactivated(val userId: UserId) : DomainEvent()
}
```

**2. Implement adapter**

```kotlin
// adapter-output-messaging/src/main/kotlin/.../KafkaEventPublisher.kt
@ApplicationScoped
class KafkaEventPublisher @Inject constructor(
    @Channel("user-events") private val emitter: Emitter<String>
) : EventPublisher {

    override fun publish(event: DomainEvent) {
        val message = when (event) {
            is DomainEvent.UserCreated -> UserCreatedMessage(...)
            is DomainEvent.UserDeactivated -> UserDeactivatedMessage(...)
        }
        emitter.send(objectMapper.writeValueAsString(message))
    }
}
```

**3. Use in use case**

```kotlin
@ApplicationScoped
class CreateUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val eventPublisher: EventPublisher
) {
    operator fun invoke(command: CreateUserCommand): UserResponse {
        // ... business logic
        val savedUser = userRepository.save(user)

        // Publish event
        eventPublisher.publish(DomainEvent.UserCreated(savedUser.id, savedUser.email))

        return UserResponse.fromDomain(savedUser)
    }
}
```

---

### Adapter Checklist

When adding a new adapter, follow this checklist:

**Input Adapter (REST, GraphQL, gRPC, CLI)**:
- [ ] Create new module `adapter-input-{name}`
- [ ] Depend on `application` and `domain`
- [ ] Inject use cases via constructor
- [ ] Convert adapter DTOs → Commands
- [ ] Call use cases
- [ ] Convert Responses → adapter DTOs
- [ ] Handle exceptions specific to adapter

**Output Adapter (Database, Cache, Message Queue, External API)**:
- [ ] Define port interface in `domain/port/output/`
- [ ] Create new module `adapter-output-{name}`
- [ ] Depend on `domain` only
- [ ] Implement port interface
- [ ] Convert domain models ↔ external format
- [ ] Handle external errors, wrap in domain exceptions

**Wiring**:
- [ ] Add module to `settings.gradle.kts`
- [ ] Add dependency in `bootstrap/build.gradle.kts`
- [ ] CDI auto-discovers implementations (no manual wiring needed!)

---

## Getting Started

### Prerequisites

- **Java 21** or higher
- **Docker** (for PostgreSQL)
- **Gradle 8.12** (wrapper included)

### Quick Start

**1. Clone the repository**

```bash
git clone <your-repo-url>
cd ms-quarkus-kt-template
```

**2. Run with dev services (easiest)**

Quarkus automatically starts PostgreSQL in Docker:

```bash
./gradlew :bootstrap:quarkusDev
```

**3. Access the application**

- **API**: http://localhost:8080/api/v1/users
- **Swagger UI**: http://localhost:8080/q/swagger-ui
- **Health**: http://localhost:8080/q/health
- **Metrics**: http://localhost:8080/q/metrics

### Manual Database Setup

If you prefer manual PostgreSQL:

```bash
docker-compose up -d postgres
./gradlew :bootstrap:quarkusDev
```

### Build for Production

```bash
# Build all modules
./gradlew build

# Build Docker image
docker build -t quarkus-kotlin-template:latest .

# Run with Docker Compose
docker-compose up --build
```

---

## API Documentation

### Endpoints

| Method | Endpoint | Description | Auth | Status |
|--------|----------|-------------|------|--------|
| POST | `/api/v1/users` | Create user | Public | 201 |
| GET | `/api/v1/users` | Get all users | user/admin | 200 |
| GET | `/api/v1/users/{id}` | Get user by ID | user/admin | 200/404 |
| PUT | `/api/v1/users/{id}` | Update user | user/admin | 200/404 |
| DELETE | `/api/v1/users/{id}` | Delete user | admin | 204/404 |

### Example Requests

**Create User**

```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

**Response (201 Created)**

```json
{
  "id": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "email": "john.doe@example.com",
  "fullName": "John Doe",
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z"
}
```

**Error Response (409 Conflict)**

```json
{
  "message": "User with email john.doe@example.com already exists",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Interactive Documentation

Visit **Swagger UI** at http://localhost:8080/q/swagger-ui to:
- View all endpoints
- Test API calls
- See request/response schemas
- Download OpenAPI spec

---

## Configuration

### Key Configuration Files

**bootstrap/src/main/resources/application.properties**

```properties
# Application
quarkus.application.name=quarkus-kotlin-template
quarkus.http.port=8080

# Database
quarkus.datasource.db-kind=postgresql
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/userdb
quarkus.datasource.username=postgres
quarkus.datasource.password=postgres

# Hibernate
quarkus.hibernate-orm.database.generation=validate
quarkus.hibernate-orm.log.sql=false

# Flyway
quarkus.flyway.migrate-at-start=true
quarkus.flyway.baseline-on-migrate=true

# OpenAPI
quarkus.swagger-ui.always-include=true
quarkus.swagger-ui.path=/q/swagger-ui

# Health & Metrics
quarkus.health.openapi.included=true
quarkus.micrometer.export.prometheus.enabled=true

# Security (disabled in dev by default)
quarkus.oidc.enabled=false

# Logging
quarkus.log.level=INFO
quarkus.log.category."com.ardikapras.template".level=DEBUG
```

### Environment-Specific Configuration

**Development** (`application.properties`)
- Dev services auto-start PostgreSQL
- Security disabled
- SQL logging enabled

**Production** (environment variables)

```bash
export QUARKUS_DATASOURCE_JDBC_URL=jdbc:postgresql://prod-db:5432/userdb
export QUARKUS_DATASOURCE_USERNAME=${DB_USER}
export QUARKUS_DATASOURCE_PASSWORD=${DB_PASSWORD}
export QUARKUS_OIDC_ENABLED=true
export QUARKUS_OIDC_AUTH_SERVER_URL=https://auth.example.com/realms/prod
export QUARKUS_LOG_LEVEL=WARN
```

---

## Testing Strategy

### Test Pyramid

```
       ┌──────────────────┐
       │  Integration     │  ← 10% (bootstrap)
       │   Tests          │
       └──────────────────┘
      ┌────────────────────┐
      │   Use Case Tests   │  ← 30% (application)
      └────────────────────┘
    ┌──────────────────────┐
    │   Domain Tests        │  ← 60% (domain)
    └──────────────────────┘
```

### 1. Domain Tests (Fast, No Dependencies)

```kotlin
// domain/src/test/kotlin/.../domain/model/UserTest.kt
class UserTest {
    @Test
    fun `should create user with valid data`() {
        val email = Email("test@example.com")
        val name = UserName("John", "Doe")

        val user = User.create(email, name)

        assertEquals(email, user.email)
        assertEquals(name, user.name)
    }

    @Test
    fun `should reject invalid email`() {
        assertThrows<IllegalArgumentException> {
            Email("invalid-email")
        }
    }
}
```

**Run**: `./gradlew :domain:test`

### 2. Use Case Tests (Mock Dependencies)

```kotlin
// application/src/test/kotlin/.../application/usecase/CreateUserUseCaseTest.kt
class CreateUserUseCaseTest {
    private lateinit var userRepository: UserRepository
    private lateinit var createUserUseCase: CreateUserUseCase

    @BeforeEach
    fun setup() {
        userRepository = mock()
        createUserUseCase = CreateUserUseCase(userRepository)
    }

    @Test
    fun `should create user`() {
        val command = CreateUserCommand("test@example.com", "John", "Doe")
        val expectedUser = User.create(Email(command.email), UserName(command.firstName, command.lastName))

        whenever(userRepository.existsByEmail(any())).thenReturn(false)
        whenever(userRepository.save(any())).thenReturn(expectedUser)

        val response = createUserUseCase(command)

        assertEquals(command.email, response.email)
        verify(userRepository).save(any())
    }
}
```

**Run**: `./gradlew :application:test`

### 3. Integration Tests (Real Infrastructure)

```kotlin
// bootstrap/src/test/kotlin/.../UserResourceTest.kt
@QuarkusTest
class UserResourceTest {
    @Test
    fun `should create and retrieve user`() {
        // Create
        val createResponse = given()
            .contentType(ContentType.JSON)
            .body(CreateUserRequest("test@example.com", "John", "Doe"))
        .`when`()
            .post("/api/v1/users")
        .then()
            .statusCode(201)
            .extract().`as`(UserResponse::class.java)

        // Retrieve
        given()
        .`when`()
            .get("/api/v1/users/${createResponse.id}")
        .then()
            .statusCode(200)
            .body("email", equalTo("test@example.com"))
    }
}
```

**Run**: `./gradlew :bootstrap:test`

### Test Databases

**Development/Testing**: H2 in-memory
**Production**: PostgreSQL

```properties
# bootstrap/src/test/resources/application.properties
quarkus.datasource.db-kind=h2
quarkus.datasource.jdbc.url=jdbc:h2:mem:testdb
```

---

## Observability

### Health Checks

**Endpoints**:
- `/q/health` - Overall health
- `/q/health/live` - Liveness (Kubernetes)
- `/q/health/ready` - Readiness (Kubernetes)

**Example Response**:

```json
{
  "status": "UP",
  "checks": [
    {
      "name": "Database connection health check",
      "status": "UP"
    }
  ]
}
```

### Metrics (Prometheus)

**Endpoint**: `/q/metrics`

**Key Metrics**:
- `http_server_requests_seconds` - Request duration
- `jvm_memory_used_bytes` - Memory usage
- `hikaricp_connections_active` - Database connections
- Custom business metrics

**Grafana Dashboard**:

```bash
docker-compose --profile monitoring up -d
# Access Grafana at http://localhost:3000 (admin/admin)
```

### Distributed Tracing (OpenTelemetry)

```properties
quarkus.otel.exporter.otlp.traces.endpoint=http://jaeger:4317
quarkus.otel.traces.enabled=true
```

Traces show complete request flow:
```
REST → CreateUserUseCase → UserRepository → Database
```

---

## Troubleshooting

### Common Issues

**1. Port 8080 already in use**

```bash
# Change port
echo "quarkus.http.port=8081" >> bootstrap/src/main/resources/application.properties
```

**2. Database connection fails**

```bash
# Check PostgreSQL
docker-compose logs postgres

# Verify connection
docker exec -it postgres psql -U postgres -d userdb
```

**3. Build fails with dependency errors**

```bash
./gradlew clean build --refresh-dependencies
```

**4. Tests fail randomly**

Likely database state issues. Ensure tests clean up:

```kotlin
@AfterEach
fun cleanup() {
    // Clean test data
}
```

**5. Quarkus dev mode doesn't reload**

```bash
# Restart dev mode
./gradlew :bootstrap:quarkusDev --no-daemon
```

---

## Development Tools

### Makefile Commands

```bash
# Build
make build

# Run dev mode
make dev

# Run tests
make test

# Build Docker image
make docker-build

# Start all services
make up

# View logs
make logs

# Clean
make clean
```

### IDE Setup

**IntelliJ IDEA** (Recommended)
1. Open project as Gradle project
2. Install Kotlin plugin
3. Enable annotation processing
4. Set JDK 21

**VS Code**
1. Install Kotlin extension
2. Install Quarkus extension
3. Configure Java 21

---

## Architecture Validation

### ArchUnit Tests

Enforce architecture rules automatically:

```kotlin
// bootstrap/src/test/kotlin/.../architecture/ArchitectureTest.kt
@ArchTest
val domainShouldNotDependOnOtherLayers =
    noClasses().that()
        .resideInAPackage("..domain..")
    .should()
        .dependOnClassesThat()
        .resideInAnyPackage("..application..", "..adapter..")

@ArchTest
val useCasesShouldOnlyDependOnDomain =
    classes().that()
        .resideInAPackage("..application.usecase..")
    .should()
        .onlyDependOnClassesThat()
        .resideInAnyPackage("..domain..", "java..", "jakarta.inject..")
```

Run: `./gradlew :bootstrap:test --tests ArchitectureTest`

---

## Best Practices

### 1. Keep Domain Pure
❌ **Don't**:
```kotlin
@Entity  // JPA annotation in domain!
data class User(...)
```

✅ **Do**:
```kotlin
// Pure Kotlin
data class User(...)
```

### 2. Use Value Objects
❌ **Don't**:
```kotlin
fun createUser(email: String)  // Can pass invalid data
```

✅ **Do**:
```kotlin
fun createUser(email: Email)  // Type-safe, validated
```

### 3. Single Responsibility Use Cases
❌ **Don't**:
```kotlin
class UserUseCase {
    fun create() {}
    fun update() {}
    fun delete() {}
}
```

✅ **Do**:
```kotlin
class CreateUserUseCase { fun invoke() {} }
class UpdateUserUseCase { fun invoke() {} }
class DeleteUserUseCase { fun invoke() {} }
```

### 4. Convert at Boundaries
❌ **Don't**:
```kotlin
// Returning JPA entity from use case
fun getUser(): UserEntity
```

✅ **Do**:
```kotlin
// Return domain model
fun getUser(): User
```

### 5. Test Appropriately
❌ **Don't**: Test domain logic via HTTP
✅ **Do**: Test domain directly, test HTTP separately

---

## Contributing

1. Follow hexagonal architecture principles
2. Write tests (domain → application → integration)
3. Run `./gradlew build` before committing
4. Update documentation for new features
5. Follow Kotlin coding conventions

---

## License

Apache License 2.0

---

## Resources

- **Hexagonal Architecture**: [Alistair Cockburn's Article](https://alistair.cockburn.us/hexagonal-architecture/)
- **Quarkus**: [Official Guides](https://quarkus.io/guides/)
- **Kotlin**: [Official Docs](https://kotlinlang.org/docs/)
- **Domain-Driven Design**: [Eric Evans' Book](https://www.domainlanguage.com/ddd/)

---

## Summary

This template provides:

- **100% hexagonal architecture compliance**
- **Production-ready** (observability, security, containerization)
- **Framework-agnostic core** (easy to migrate)
- **Testable** (domain/app layers test in milliseconds)
- **Extensible** (add adapters without touching core)
- **Developer-friendly** (live reload, clear structure)

**Start building your microservice on a solid foundation!** 🚀
