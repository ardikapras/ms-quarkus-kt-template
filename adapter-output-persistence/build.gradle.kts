import io.quarkus.gradle.tasks.QuarkusDev
import org.gradle.kotlin.dsl.withType

// Persistence adapter module - Output adapter for database

plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.kotlinAllopen)
    alias(libs.plugins.kotlinJpa)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.quarkus)
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.enterprise.context.ApplicationScoped")
}

tasks.withType<QuarkusDev> {
    // only app should start when run quarkusDev task
    enabled = false
}

dependencies {
    // Depend on domain module using type-safe project accessor
    implementation(projects.domain)

    implementation(libs.bundles.kotlinxEcosystem)
    implementation(enforcedPlatform(libs.quarkusBom))
    implementation(libs.quarkusKotlin)

    // Quarkus Hibernate with Panache Kotlin
    implementation(libs.quarkusHibernateOrmPanacheKotlin)

    // PostgreSQL driver
    implementation(libs.quarkusJdbcPostgresql)

    // Flyway for database migrations
    implementation(libs.quarkusFlyway)

    testImplementation(libs.bundles.unitTesting)
    testImplementation(libs.quarkusJunit)
}
