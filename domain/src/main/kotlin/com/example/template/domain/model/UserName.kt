package com.example.template.domain.model

/**
 * Value object representing a user's full name
 */
data class UserName(
    val firstName: String,
    val lastName: String
) {
    init {
        require(firstName.isNotBlank()) { "First name cannot be blank" }
        require(lastName.isNotBlank()) { "Last name cannot be blank" }
        require(firstName.length <= 50) { "First name cannot exceed 50 characters" }
        require(lastName.length <= 50) { "Last name cannot exceed 50 characters" }
    }

    fun fullName(): String = "$firstName $lastName"
}
