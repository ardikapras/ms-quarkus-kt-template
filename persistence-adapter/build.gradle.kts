// Persistence adapter module - Output adapter for database

plugins {
    kotlin("plugin.allopen")
    kotlin("plugin.jpa")
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.enterprise.context.ApplicationScoped")
}

dependencies {
    // Depend on domain module
    implementation(project(":domain"))

    // Quarkus Hibernate with Panache Kotlin
    implementation("io.quarkus:quarkus-hibernate-orm-panache-kotlin")

    // PostgreSQL driver
    implementation("io.quarkus:quarkus-jdbc-postgresql")

    // Flyway for database migrations
    implementation("io.quarkus:quarkus-flyway")
}
