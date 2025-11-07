package com.ardikapras.template.application.usecase

import com.ardikapras.template.domain.exception.UserNotFoundException
import com.ardikapras.template.domain.model.UserId
import com.ardikapras.template.domain.port.output.UserRepository
import jakarta.enterprise.context.ApplicationScoped

/**
 * Use case for deleting a user
 */
@ApplicationScoped
class DeleteUserUseCase(
    private val userRepository: UserRepository,
) {
    operator fun invoke(userId: String) {
        val id = UserId.fromString(userId)

        // Verify user exists before deleting
        userRepository.findById(id)
            ?: throw UserNotFoundException(id)

        userRepository.deleteById(id)
    }
}
