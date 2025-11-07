package com.ardikapras.template.domain.port.output

import com.ardikapras.template.domain.model.Email
import com.ardikapras.template.domain.model.User
import com.ardikapras.template.domain.model.UserId

/**
 * Repository port (interface) for User aggregate
 * This is an output port that will be implemented by the persistence adapter
 */
interface UserRepository {
    /**
     * Saves a user (create or update)
     */
    fun save(user: User): User

    /**
     * Finds a user by ID
     */
    fun findById(id: UserId): User?

    /**
     * Finds a user by email
     */
    fun findByEmail(email: Email): User?

    /**
     * Finds all users
     */
    fun getAllUsers(): List<User>

    /**
     * Deletes a user by ID
     */
    fun deleteById(id: UserId)

    /**
     * Checks if a user exists by email
     */
    fun existsByEmail(email: Email): Boolean
}
