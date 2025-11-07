package com.ardikapras.template.application.usecase

import com.ardikapras.template.application.dto.UserResponse
import com.ardikapras.template.domain.port.output.UserRepository
import jakarta.enterprise.context.ApplicationScoped

/**
 * Use case for retrieving all users
 */
@ApplicationScoped
class GetAllUsersUseCase(
    private val userRepository: UserRepository,
) {
    operator fun invoke(): List<UserResponse> =
        userRepository
            .getAllUsers()
            .map { UserResponse.fromDomain(it) }
}
