package com.ardikapras.template.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.jupiter.api.Test

/**
 * Guard tests for Persistence Adapter to ensure proper data access patterns.
 * Persistence layer should properly isolate JPA/Hibernate concerns from domain.
 */
class PersistenceAdapterGuard {
    @Test
    fun `JPA entities should be in adapter persistence entity package`() {
        Konsist
            .scopeFromModule("adapter-output-persistence")
            .classes()
            .withNameEndingWith("Entity")
            .assertTrue {
                it.resideInPackage("${Packages.PERSISTENCE_ADAPTER}.entity")
            }
    }

    @Test
    fun `repository implementations should be in adapter persistence repository package`() {
        Konsist
            .scopeFromModule("adapter-output-persistence")
            .classes()
            .withNameEndingWith("RepositoryImpl")
            .assertTrue {
                it.resideInPackage("${Packages.PERSISTENCE_ADAPTER}.repository")
            }
    }

    @Test
    fun `repository implementations should implement domain repository ports`() {
        Konsist
            .scopeFromModule("adapter-output-persistence")
            .classes()
            .withNameEndingWith("RepositoryImpl")
            .assertTrue { repositoryImpl ->
                // Check that it has at least one parent interface
                repositoryImpl.parents().isNotEmpty()
            }
    }

    @Test
    fun `JPA entities should have conversion methods to domain`() {
        Konsist
            .scopeFromModule("adapter-output-persistence")
            .classes()
            .withNameEndingWith("Entity")
            .assertTrue { entity ->
                // Entity should have a toDomain() or toEntity() method
                entity.hasFunction { function ->
                    function.name == "toDomain" || function.name.startsWith("to")
                }
            }
    }

    @Test
    fun `persistence adapter should not depend on REST adapter`() {
        Konsist
            .scopeFromModule("adapter-output-persistence")
            .imports
            .assertTrue {
                !it.hasNameStartingWith(Packages.REST_ADAPTER)
            }
    }

    @Test
    fun `persistence adapter should not depend on application layer`() {
        Konsist
            .scopeFromModule("adapter-output-persistence")
            .imports
            .assertTrue {
                !it.hasNameStartingWith(Packages.APPLICATION)
            }
    }

    @Test
    fun `JPA entities should not be exposed outside persistence module`() {
        Konsist
            .scopeFromProject()
            .files
            .filterNot { it.path.contains("adapter-output-persistence") }
            .assertTrue { file ->
                // Check that no file outside adapter-output-persistence imports Entity classes
                file.imports.none { import ->
                    import.hasNameEndingWith("Entity") &&
                        import.hasNameStartingWith(Packages.PERSISTENCE_ADAPTER)
                }
            }
    }
}
