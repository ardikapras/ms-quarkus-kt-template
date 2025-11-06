package com.example.template.application.usecase

import com.example.template.application.dto.UserResponse
import com.example.template.domain.port.UserRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

/**
 * Use case for retrieving all users
 */
@ApplicationScoped
class GetAllUsersUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    fun execute(): List<UserResponse> {
        return userRepository.findAll()
            .map { UserResponse.fromDomain(it) }
    }
}
