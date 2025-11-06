package com.example.template.application.dto

/**
 * Command to create a new user
 */
data class CreateUserCommand(
    val email: String,
    val firstName: String,
    val lastName: String
)
