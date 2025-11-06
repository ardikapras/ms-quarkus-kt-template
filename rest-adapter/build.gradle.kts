// REST adapter module - Input adapter for REST API

plugins {
    kotlin("plugin.allopen")
}

allOpen {
    annotation("jakarta.ws.rs.Path")
    annotation("jakarta.enterprise.context.ApplicationScoped")
}

dependencies {
    // Depend on application module
    implementation(project(":application"))
    implementation(project(":domain"))

    // Quarkus REST
    implementation("io.quarkus:quarkus-rest")
    implementation("io.quarkus:quarkus-rest-jackson")

    // OpenAPI/Swagger
    implementation("io.quarkus:quarkus-smallrye-openapi")

    // Bean Validation
    implementation("io.quarkus:quarkus-hibernate-validator")

    // Security
    implementation("io.quarkus:quarkus-oidc")
}
