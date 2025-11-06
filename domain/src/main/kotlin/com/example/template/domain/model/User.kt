package com.example.template.domain.model

import java.time.Instant

/**
 * User aggregate root - represents a user in the domain
 */
data class User(
    val id: UserId,
    val email: Email,
    val name: UserName,
    val active: Boolean = true,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
) {
    /**
     * Updates the user's name
     */
    fun updateName(newName: UserName): User {
        return copy(
            name = newName,
            updatedAt = Instant.now()
        )
    }

    /**
     * Updates the user's email
     */
    fun updateEmail(newEmail: Email): User {
        return copy(
            email = newEmail,
            updatedAt = Instant.now()
        )
    }

    /**
     * Deactivates the user
     */
    fun deactivate(): User {
        return copy(
            active = false,
            updatedAt = Instant.now()
        )
    }

    /**
     * Activates the user
     */
    fun activate(): User {
        return copy(
            active = true,
            updatedAt = Instant.now()
        )
    }

    companion object {
        /**
         * Factory method to create a new user
         */
        fun create(
            email: Email,
            name: UserName
        ): User {
            return User(
                id = UserId.generate(),
                email = email,
                name = name
            )
        }
    }
}
