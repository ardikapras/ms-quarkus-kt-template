package com.example.template.application.usecase

import com.example.template.application.dto.CreateUserCommand
import com.example.template.application.dto.UserResponse
import com.example.template.domain.exception.UserAlreadyExistsException
import com.example.template.domain.model.Email
import com.example.template.domain.model.User
import com.example.template.domain.model.UserName
import com.example.template.domain.port.UserRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

/**
 * Use case for creating a new user
 */
@ApplicationScoped
class CreateUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    fun execute(command: CreateUserCommand): UserResponse {
        val email = Email(command.email)

        // Check if user already exists
        if (userRepository.existsByEmail(email)) {
            throw UserAlreadyExistsException(email)
        }

        // Create user
        val user = User.create(
            email = email,
            name = UserName(
                firstName = command.firstName,
                lastName = command.lastName
            )
        )

        // Save and return
        val savedUser = userRepository.save(user)
        return UserResponse.fromDomain(savedUser)
    }
}
