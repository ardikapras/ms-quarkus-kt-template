package com.example.template.domain.model

import java.util.UUID

/**
 * Value object representing a unique user identifier
 */
@JvmInline
value class UserId(val value: UUID) {
    companion object {
        fun generate(): UserId = UserId(UUID.randomUUID())

        fun fromString(value: String): UserId = UserId(UUID.fromString(value))
    }

    override fun toString(): String = value.toString()
}
