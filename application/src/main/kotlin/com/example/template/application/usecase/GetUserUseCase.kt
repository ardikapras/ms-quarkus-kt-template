package com.example.template.application.usecase

import com.example.template.application.dto.UserResponse
import com.example.template.domain.exception.UserNotFoundException
import com.example.template.domain.model.UserId
import com.example.template.domain.port.UserRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

/**
 * Use case for retrieving a user by ID
 */
@ApplicationScoped
class GetUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    fun execute(userId: String): UserResponse {
        val id = UserId.fromString(userId)
        val user = userRepository.findById(id)
            ?: throw UserNotFoundException(id)

        return UserResponse.fromDomain(user)
    }
}
