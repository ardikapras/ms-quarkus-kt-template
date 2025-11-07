package com.ardikapras.template.application.usecase

import com.ardikapras.template.application.dto.UpdateUserCommand
import com.ardikapras.template.application.dto.UserResponse
import com.ardikapras.template.domain.exception.UserNotFoundException
import com.ardikapras.template.domain.model.Email
import com.ardikapras.template.domain.model.UserId
import com.ardikapras.template.domain.model.UserName
import com.ardikapras.template.domain.port.output.UserRepository
import jakarta.enterprise.context.ApplicationScoped

/**
 * Use case for updating an existing user
 */
@ApplicationScoped
class UpdateUserUseCase(
    private val userRepository: UserRepository,
) {
    operator fun invoke(command: UpdateUserCommand): UserResponse {
        val id = UserId.fromString(command.userId)
        var user =
            userRepository.findById(id)
                ?: throw UserNotFoundException(id)

        // Update email if provided
        command.email?.let {
            user = user.updateEmail(Email(it))
        }

        // Update name if provided
        if (command.firstName != null && command.lastName != null) {
            user =
                user.updateName(
                    UserName(
                        firstName = command.firstName,
                        lastName = command.lastName,
                    ),
                )
        }

        val updatedUser = userRepository.save(user)
        return UserResponse.fromDomain(updatedUser)
    }
}
