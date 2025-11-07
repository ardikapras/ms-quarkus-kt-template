package com.ardikapras.template.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import org.junit.jupiter.api.Test

/**
 * Architecture fitness function tests that enforce hexagonal architecture boundaries.
 * These tests ensure that:
 * - Domain layer has no external dependencies
 * - Application layer only depends on domain
 * - Adapters depend on domain and application appropriately
 * - Layers respect the hexagonal architecture principles
 */
class ArchitectureGuard {
    @Test
    fun `hexagonal architecture dependencies are correct`() {
        Konsist.scopeFromProject().assertArchitecture {
            val domain = Layer("Domain", Packages.DOMAIN.wildcard())
            val application = Layer("Application", Packages.APPLICATION.wildcard())
            val restAdapter = Layer("REST Adapter", Packages.REST_ADAPTER.wildcard())
            val persistenceAdapter = Layer("Persistence Adapter", Packages.PERSISTENCE_ADAPTER.wildcard())

            // Domain is the core - depends on nothing
            domain.dependsOnNothing()

            // Application layer only depends on domain
            application.dependsOn(domain)

            // REST adapter (input port) depends on application and domain
            restAdapter.dependsOn(application, domain)

            // Persistence adapter (output port) only depends on domain
            persistenceAdapter.dependsOn(domain)
        }
    }

    @Test
    fun `adapters should not depend on each other`() {
        Konsist.scopeFromProject().assertArchitecture {
            val restAdapter = Layer("REST Adapter", Packages.REST_ADAPTER.wildcard())
            val persistenceAdapter = Layer("Persistence Adapter", Packages.PERSISTENCE_ADAPTER.wildcard())

            // REST adapter should not know about persistence
            restAdapter.doesNotDependOn(persistenceAdapter)

            // Persistence adapter should not know about REST
            persistenceAdapter.doesNotDependOn(restAdapter)
        }
    }

    @Test
    fun `domain should not depend on application layer`() {
        Konsist.scopeFromProject().assertArchitecture {
            val domain = Layer("Domain", Packages.DOMAIN.wildcard())
            val application = Layer("Application", Packages.APPLICATION.wildcard())

            // Ensure domain doesn't accidentally depend on application
            domain.doesNotDependOn(application)
        }
    }

    @Test
    fun `application should not depend on adapters`() {
        Konsist.scopeFromProject().assertArchitecture {
            val application = Layer("Application", Packages.APPLICATION.wildcard())
            val restAdapter = Layer("REST Adapter", Packages.REST_ADAPTER.wildcard())
            val persistenceAdapter = Layer("Persistence Adapter", Packages.PERSISTENCE_ADAPTER.wildcard())

            // Application should not know about specific adapter implementations
            application.doesNotDependOn(restAdapter, persistenceAdapter)
        }
    }
}
