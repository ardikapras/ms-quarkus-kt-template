package com.ardikapras.template.domain.exception

import com.ardikapras.template.domain.model.Email

/**
 * Exception thrown when attempting to create a user with an email that already exists
 */
class UserAlreadyExistsException(
    email: Email,
) : DomainException(
        message = "User already exists with email: $email",
    )
