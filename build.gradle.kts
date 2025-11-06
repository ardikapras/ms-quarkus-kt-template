plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.allopen") version "2.1.0"
    kotlin("plugin.jpa") version "2.1.0"
    id("io.quarkus") version "3.29.0"
}

allprojects {
    group = "com.example.template"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
        mavenLocal()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    dependencies {
        implementation(enforcedPlatform("io.quarkus.platform:quarkus-bom:3.29.0"))
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        implementation("io.quarkus:quarkus-kotlin")

        testImplementation("io.quarkus:quarkus-junit5")
        testImplementation("io.rest-assured:rest-assured")
        testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "21"
            javaParameters = true
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }

    tasks.withType<Test> {
        systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

// Root project has no source code
tasks.named("quarkusBuild") {
    enabled = false
}
