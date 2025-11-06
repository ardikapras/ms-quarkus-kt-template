// Application module - Use cases and business logic orchestration

dependencies {
    // Depend on domain module
    implementation(project(":domain"))

    // Jakarta annotations for CDI
    implementation("jakarta.enterprise:jakarta.enterprise.cdi-api:4.1.0")
    implementation("jakarta.inject:jakarta.inject-api:2.0.1")
}
