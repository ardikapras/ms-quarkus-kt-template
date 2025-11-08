// Bootstrap module - Main application entry point

plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.kotlinAllopen)
    alias(libs.plugins.quarkus)
}

tasks.named("test") {
    dependsOn("quarkusBuild")
}

allOpen {
    annotation("jakarta.ws.rs.Path")
    annotation("jakarta.enterprise.context.ApplicationScoped")
    annotation("jakarta.persistence.Entity")
}

dependencies {
    // Depend on all modules using type-safe project accessors
    implementation(projects.domain)
    implementation(projects.application)
    implementation(projects.adapterInputRest)
    implementation(projects.adapterOutputPersistence)

    implementation(enforcedPlatform(libs.quarkusBom))
    implementation(libs.bundles.kotlinxEcosystem)
    implementation(libs.quarkusKotlin)
    implementation(libs.quarkusVertx)
    implementation(libs.quarkusArc)
    implementation(libs.quarkusMutiny)

    // Docker and Kubernetes
    implementation(libs.quarkusJib)
    implementation(libs.quarkusKubernetes)
    implementation(libs.quarkusSmallryeHealth)
    implementation(libs.quarkusMinikube)
    implementation(libs.quarkusHelm)

    // Testing
    testImplementation(libs.bundles.unitTesting)
    testImplementation(libs.bundles.quarkusTesting)
    testImplementation(libs.bundles.testcontainers)

    // Health checks
    implementation(libs.quarkusSmallryeHealth)

    // Metrics
    implementation(libs.quarkusMicrometerRegistryPrometheus)

    // OpenTelemetry tracing
    implementation(libs.quarkusOpentelemetry)

    // OpenAPI/Swagger documentation
    implementation(libs.quarkusSmallryeOpenapi)
}
