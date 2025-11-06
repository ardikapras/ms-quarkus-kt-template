package com.example.template.application.usecase

import com.example.template.domain.exception.UserNotFoundException
import com.example.template.domain.model.UserId
import com.example.template.domain.port.UserRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

/**
 * Use case for deleting a user
 */
@ApplicationScoped
class DeleteUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    fun execute(userId: String) {
        val id = UserId.fromString(userId)

        // Verify user exists before deleting
        userRepository.findById(id)
            ?: throw UserNotFoundException(id)

        userRepository.deleteById(id)
    }
}
