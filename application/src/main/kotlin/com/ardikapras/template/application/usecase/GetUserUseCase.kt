package com.ardikapras.template.application.usecase

import com.ardikapras.template.application.dto.UserResponse
import com.ardikapras.template.domain.exception.UserNotFoundException
import com.ardikapras.template.domain.model.UserId
import com.ardikapras.template.domain.port.output.UserRepository
import jakarta.enterprise.context.ApplicationScoped

/**
 * Use case for retrieving a user by ID
 */
@ApplicationScoped
class GetUserUseCase(
    private val userRepository: UserRepository,
) {
    operator fun invoke(userId: String): UserResponse {
        val id = UserId.fromString(userId)
        val user =
            userRepository.findById(id)
                ?: throw UserNotFoundException(id)

        return UserResponse.fromDomain(user)
    }
}
