package com.ardikapras.template

import io.quarkus.runtime.Quarkus
import io.quarkus.runtime.annotations.QuarkusMain

/**
 * Main application entry point.
 *
 * This is the bootstrap module that wires together all hexagonal architecture layers:
 * - Domain (core business logic)
 * - Application (use cases)
 * - API adapters (REST, gRPC, etc.)
 * - Persistence adapters (PostgreSQL, MongoDB, etc.)
 */
@QuarkusMain
object Application {
    @JvmStatic
    fun main(args: Array<String>) {
        Quarkus.run(*args)
    }
}
