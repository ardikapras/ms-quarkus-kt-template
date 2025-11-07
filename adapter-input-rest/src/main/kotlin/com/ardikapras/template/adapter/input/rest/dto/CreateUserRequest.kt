package com.ardikapras.template.adapter.input.rest.dto

import com.ardikapras.template.application.dto.CreateUserCommand
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * REST request DTO for creating a user
 */
data class CreateUserRequest(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    val email: String,
    @field:NotBlank(message = "First name is required")
    @field:Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    val firstName: String,
    @field:NotBlank(message = "Last name is required")
    @field:Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    val lastName: String,
) {
    fun toCommand(): CreateUserCommand =
        CreateUserCommand(
            email = email.trim(),
            firstName = firstName.trim(),
            lastName = lastName.trim(),
        )
}
