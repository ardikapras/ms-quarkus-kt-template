package com.example.template.domain.exception

import com.example.template.domain.model.UserId

/**
 * Exception thrown when a user is not found
 */
class UserNotFoundException(userId: UserId) : DomainException(
    message = "User not found with id: $userId"
)
