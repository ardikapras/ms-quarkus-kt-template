package com.ardikapras.template.application.dto

/**
 * Command to update an existing user
 */
data class UpdateUserCommand(
    val userId: String,
    val email: String?,
    val firstName: String?,
    val lastName: String?,
)
