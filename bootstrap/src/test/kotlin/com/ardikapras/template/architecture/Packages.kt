package com.ardikapras.template.architecture

/**
 * Defines package names for the application modules.
 * Used by Konsist architecture tests to enforce hexagonal architecture boundaries.
 */
object Packages {
    const val DOMAIN = "com.ardikapras.template.domain"
    const val APPLICATION = "com.ardikapras.template.application"
    const val REST_ADAPTER = "com.ardikapras.template.adapter.input.rest"
    const val PERSISTENCE_ADAPTER = "com.ardikapras.template.adapter.output.persistence"
}

/**
 * Extension function to create a wildcard pattern for package matching.
 * Example: "com.example.template.domain".wildcard() -> "com.example.template.domain.."
 */
fun String.wildcard() = "$this.."
