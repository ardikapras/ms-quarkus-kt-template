// Bootstrap module - Main application entry point

plugins {
    kotlin("plugin.allopen")
    id("io.quarkus")
}

allOpen {
    annotation("jakarta.ws.rs.Path")
    annotation("jakarta.enterprise.context.ApplicationScoped")
    annotation("jakarta.persistence.Entity")
}

dependencies {
    // Depend on all modules
    implementation(project(":domain"))
    implementation(project(":application"))
    implementation(project(":rest-adapter"))
    implementation(project(":persistence-adapter"))

    // Quarkus core
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-config-yaml")

    // Health checks
    implementation("io.quarkus:quarkus-smallrye-health")

    // Metrics
    implementation("io.quarkus:quarkus-micrometer-registry-prometheus")

    // OpenTelemetry tracing
    implementation("io.quarkus:quarkus-opentelemetry")

    // Testing
    testImplementation("io.quarkus:quarkus-junit5-mockito")
    testImplementation("io.rest-assured:kotlin-extensions")
    testImplementation("io.quarkus:quarkus-jdbc-h2")
}
