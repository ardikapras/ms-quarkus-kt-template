package com.ardikapras.template.application.usecase

import com.ardikapras.template.application.dto.CreateUserCommand
import com.ardikapras.template.application.dto.UserResponse
import com.ardikapras.template.domain.exception.UserAlreadyExistsException
import com.ardikapras.template.domain.model.Email
import com.ardikapras.template.domain.model.User
import com.ardikapras.template.domain.model.UserName
import com.ardikapras.template.domain.port.output.UserRepository
import jakarta.enterprise.context.ApplicationScoped

/**
 * Use case for creating a new user
 */
@ApplicationScoped
class CreateUserUseCase(
    private val userRepository: UserRepository,
) {
    operator fun invoke(command: CreateUserCommand): UserResponse {
        val email = Email(command.email)

        // Check if user already exists
        if (userRepository.existsByEmail(email)) {
            throw UserAlreadyExistsException(email)
        }

        // Create user
        val user =
            User.create(
                email = email,
                name =
                    UserName(
                        firstName = command.firstName,
                        lastName = command.lastName,
                    ),
            )

        // Save and return
        val savedUser = userRepository.save(user)
        return UserResponse.fromDomain(savedUser)
    }
}
