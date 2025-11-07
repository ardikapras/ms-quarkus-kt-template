package com.ardikapras.template.adapter.input.rest.dto

import com.ardikapras.template.application.dto.UpdateUserCommand
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size

/**
 * REST request DTO for updating a user
 */
data class UpdateUserRequest(
    @field:Email(message = "Invalid email format")
    val email: String? = null,
    @field:Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    val firstName: String? = null,
    @field:Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    val lastName: String? = null,
) {
    fun toCommand(userId: String): UpdateUserCommand =
        UpdateUserCommand(
            userId = userId,
            email = email?.trim(),
            firstName = firstName?.trim(),
            lastName = lastName?.trim(),
        )
}
