// Application module - Use cases and business logic orchestration

dependencies {
    // Depend on domain module using type-safe project accessor
    implementation(projects.domain)

    // Jakarta Inject API for dependency injection annotations
    implementation(libs.jakartaInjectApi)

    // Quarkus Arc for @ApplicationScoped annotation (compile-only, provided by Quarkus at runtime)
    compileOnly(enforcedPlatform(libs.quarkusBom))
    compileOnly(libs.quarkusArc)
}
