package com.ardikapras.template.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.jupiter.api.Test

/**
 * Guard tests for REST Adapter to ensure proper API design and boundaries.
 * REST resources should not leak domain objects directly.
 */
class RestAdapterGuard {
    @Test
    fun `REST resources should be annotated with Path`() {
        Konsist
            .scopeFromModule("adapter-input-rest")
            .classes()
            .withNameEndingWith("Resource")
            .assertTrue {
                it.annotations.any { annotation ->
                    annotation.name == "Path"
                }
            }
    }

    @Test
    fun `REST resources should reside in adapter rest package`() {
        Konsist
            .scopeFromModule("adapter-input-rest")
            .classes()
            .withNameEndingWith("Resource")
            .assertTrue {
                it.resideInPackage("${Packages.REST_ADAPTER}..")
            }
    }

    @Test
    fun `REST DTOs should be in adapter rest dto package`() {
        Konsist
            .scopeFromModule("adapter-input-rest")
            .classes()
            .withNameEndingWith("Request", "Response", "ErrorResponse")
            .assertTrue {
                it.resideInPackage("${Packages.REST_ADAPTER}.dto")
            }
    }

    @Test
    fun `REST resources should not import domain model classes`() {
        Konsist
            .scopeFromModule("adapter-input-rest")
            .files
            .filter { it.name.endsWith("Resource.kt") }
            .assertTrue { file ->
                // Check that REST resources don't import domain.model classes
                file.imports.none { import ->
                    import.hasNameStartingWith("${Packages.DOMAIN}.model")
                }
            }
    }

    @Test
    fun `exception handlers should be in adapter rest exception package`() {
        Konsist
            .scopeFromModule("adapter-input-rest")
            .classes()
            .withNameEndingWith("Handler", "Mapper")
            .assertTrue {
                it.resideInPackage("${Packages.REST_ADAPTER}.exception")
            }
    }

    @Test
    fun `REST adapter should not depend on persistence adapter`() {
        Konsist
            .scopeFromModule("adapter-input-rest")
            .imports
            .assertTrue {
                !it.hasNameStartingWith(Packages.PERSISTENCE_ADAPTER)
            }
    }
}
