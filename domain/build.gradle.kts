// Domain module - Pure business logic with no external dependencies
// No Quarkus or framework dependencies here

dependencies {
    // Jakarta Inject API for @Singleton annotation (needed for use cases)
    // This is just the API, not an implementation - keeping domain pure
    implementation(libs.jakartaInjectApi)
}
