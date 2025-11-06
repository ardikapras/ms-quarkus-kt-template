pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
    plugins {
        id("io.quarkus") version "3.29.0"
        kotlin("jvm") version "2.1.0"
        kotlin("plugin.allopen") version "2.1.0"
        kotlin("plugin.jpa") version "2.1.0"
    }
}

rootProject.name = "ms-quarkus-kt-template"

include(
    "domain",
    "application",
    "rest-adapter",
    "persistence-adapter",
    "bootstrap"
)
