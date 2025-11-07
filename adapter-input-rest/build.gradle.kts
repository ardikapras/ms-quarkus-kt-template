import io.quarkus.gradle.tasks.QuarkusDev
import org.gradle.kotlin.dsl.withType

// REST adapter module - Input adapter for REST API

plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.kotlinAllopen)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.quarkus)
}

allOpen {
    annotation("jakarta.ws.rs.Path")
    annotation("jakarta.enterprise.context.ApplicationScoped")
}

tasks.withType<QuarkusDev> {
    // only app should start when run quarkusDev task
    enabled = false
}

dependencies {
    implementation(projects.application)
    implementation(projects.domain)

    implementation(libs.bundles.kotlinxEcosystem)
    implementation(libs.arrowKt)
    implementation(enforcedPlatform(libs.quarkusBom))
    implementation(libs.quarkusKotlin)
    implementation(libs.quarkusRest)
    implementation(libs.quarkusRestJackson)
    implementation(libs.jacksonKotlin)

    testImplementation(libs.bundles.unitTesting)
    testImplementation(libs.bundles.quarkusTesting)

    // Bean Validation
    implementation(libs.quarkusHibernateValidator)

    // Security
    implementation(libs.quarkusOidc)

    // OpenAPI/Swagger annotations
    compileOnly(libs.quarkusSmallryeOpenapi)
}
