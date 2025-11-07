package com.ardikapras.template.application.dto

import com.ardikapras.template.domain.model.User
import java.time.Instant

/**
 * Response DTO for User
 */
data class UserResponse(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val fullName: String,
    val active: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    companion object {
        fun fromDomain(user: User): UserResponse =
            UserResponse(
                id = user.id.toString(),
                email = user.email.value,
                firstName = user.name.firstName,
                lastName = user.name.lastName,
                fullName = user.name.fullName(),
                active = user.active,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt,
            )
    }
}
