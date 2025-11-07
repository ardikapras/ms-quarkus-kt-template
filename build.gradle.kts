plugins {
    id("buildsrc.convention.kotlin-jvm") apply false
    id("java")
    alias(libs.plugins.ktlint) apply false
}

allprojects {
    group = "com.ardikapras.template"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
        mavenLocal()
    }
}

subprojects {
    apply(plugin = "buildsrc.convention.kotlin-jvm")
    apply(plugin = "java")
    apply(
        plugin =
            rootProject.libs.plugins.ktlint
                .get()
                .pluginId,
    )

    tasks {
        withType<Test> {
            useJUnitPlatform()
        }
    }
}

// Git hooks auto-installation
tasks.register<Copy>("copyGitHooks") {
    description = "Copies git hooks from /hooks to .git/hooks folder"
    group = "git hooks"
    val destinationDir = "$rootDir/.git/hooks/"
    from("$rootDir/hooks/")
    into(destinationDir)

    doFirst {
        delete(destinationDir)
    }
}

val installGitHooks =
    tasks.register<Exec>("installGitHooks") {
        description = "Installs git hooks and makes them executable"
        group = "git hooks"
        workingDir = rootDir
        commandLine = listOf("chmod")
        args("-R", "+x", ".git/hooks/")
        dependsOn("copyGitHooks")
        doLast {
            logger.lifecycle("✅ Git hooks installed successfully")
        }
    }

tasks.assemble {
    dependsOn(installGitHooks)
}

dependencies {
    implementation(projects.bootstrap)
    implementation(libs.bundles.kotlinxEcosystem)
    implementation(libs.bundles.unitTesting)
    testImplementation(libs.konsist)
}
