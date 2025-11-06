package com.example.template.adapter.rest.dto

import java.time.Instant

/**
 * Standard error response for REST API
 */
data class ErrorResponse(
    val timestamp: Instant = Instant.now(),
    val status: Int,
    val error: String,
    val message: String,
    val path: String? = null,
    val validationErrors: Map<String, String>? = null
)
