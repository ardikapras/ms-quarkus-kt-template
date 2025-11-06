package com.example.template.application.usecase

import com.example.template.application.dto.UpdateUserCommand
import com.example.template.application.dto.UserResponse
import com.example.template.domain.exception.UserNotFoundException
import com.example.template.domain.model.Email
import com.example.template.domain.model.UserId
import com.example.template.domain.model.UserName
import com.example.template.domain.port.UserRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

/**
 * Use case for updating an existing user
 */
@ApplicationScoped
class UpdateUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    fun execute(command: UpdateUserCommand): UserResponse {
        val id = UserId.fromString(command.userId)
        var user = userRepository.findById(id)
            ?: throw UserNotFoundException(id)

        // Update email if provided
        command.email?.let {
            user = user.updateEmail(Email(it))
        }

        // Update name if provided
        if (command.firstName != null && command.lastName != null) {
            user = user.updateName(
                UserName(
                    firstName = command.firstName,
                    lastName = command.lastName
                )
            )
        }

        val updatedUser = userRepository.save(user)
        return UserResponse.fromDomain(updatedUser)
    }
}
