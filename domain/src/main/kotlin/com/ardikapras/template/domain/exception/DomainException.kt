package com.ardikapras.template.domain.exception

/**
 * Base exception for all domain-level exceptions
 */
abstract class DomainException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
