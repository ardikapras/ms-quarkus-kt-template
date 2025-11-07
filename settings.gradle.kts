pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
}

// Enable type-safe project accessors (projects.domain instead of project(":domain"))
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "ms-quarkus-kt-template"

include(":bootstrap")
include(":adapter-output-persistence")
include(":domain")
include(":application")
include(":adapter-input-rest")
