package com.ardikapras.template.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import com.lemonappdev.konsist.api.verify.assertEmpty
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.jupiter.api.Test

/**
 * Guard tests for the Domain layer to ensure it remains pure and framework-free.
 * The domain should only depend on Kotlin/Java standard libraries.
 */
class DomainGuard {
    @Test
    fun `domain module should have dependencies only on kotlin and java`() {
        Konsist
            .scopeFromProduction("domain")
            .imports
            .filterNot { it.hasNameStartingWith(Packages.DOMAIN) }
            .filterNot { it.hasNameStartingWith("kotlin") }
            .filterNot { it.hasNameStartingWith("java") }
            .assertEmpty()
    }

    @Test
    fun `domain exceptions should extend DomainException`() {
        Konsist
            .scopeFromModule("domain")
            .classes()
            .withNameEndingWith("Exception")
            .filterNot { it.name == "DomainException" }
            .assertTrue {
                it.hasParentWithName("DomainException")
            }
    }

    @Test
    fun `domain model classes should be data classes or sealed classes`() {
        Konsist
            .scopeFromPackage("${Packages.DOMAIN}.model")
            .classes()
            .filterNot { it.hasAnnotationOf(JvmInline::class) } // Exclude value classes
            .assertTrue {
                it.hasDataModifier || it.hasSealedModifier
            }
    }

    @Test
    fun `repository interfaces should be in port package`() {
        Konsist
            .scopeFromModule("domain")
            .interfaces()
            .withNameEndingWith("Repository")
            .assertTrue {
                it.resideInPackage("${Packages.DOMAIN}.port..")
            }
    }

    @Test
    fun `domain should not have any annotations from frameworks`() {
        Konsist
            .scopeFromModule("domain")
            .classes()
            .assertTrue {
                val annotations = it.annotations
                annotations.none { annotation ->
                    annotation.fullyQualifiedName?.startsWith("jakarta.persistence") == true ||
                        annotation.fullyQualifiedName?.startsWith("jakarta.transaction") == true ||
                        annotation.fullyQualifiedName?.startsWith("org.hibernate") == true ||
                        annotation.fullyQualifiedName?.startsWith("io.quarkus") == true ||
                        annotation.fullyQualifiedName?.startsWith("com.fasterxml.jackson") == true
                }
            }
    }
}
