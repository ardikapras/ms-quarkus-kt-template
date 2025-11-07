package com.ardikapras.template.adapter.input.rest.exception

import com.ardikapras.template.adapter.input.rest.dto.ErrorResponse
import com.ardikapras.template.domain.exception.UserAlreadyExistsException
import com.ardikapras.template.domain.exception.UserNotFoundException
import jakarta.validation.ConstraintViolationException
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider

/**
 * Exception handler for UserNotFoundException
 */
@Provider
class UserNotFoundExceptionMapper : ExceptionMapper<UserNotFoundException> {
    override fun toResponse(exception: UserNotFoundException): Response {
        val errorResponse =
            ErrorResponse(
                status = 404,
                error = "Not Found",
                message = exception.message ?: "User not found",
            )
        return Response.status(Response.Status.NOT_FOUND).entity(errorResponse).build()
    }
}

/**
 * Exception handler for UserAlreadyExistsException
 */
@Provider
class UserAlreadyExistsExceptionMapper : ExceptionMapper<UserAlreadyExistsException> {
    override fun toResponse(exception: UserAlreadyExistsException): Response {
        val errorResponse =
            ErrorResponse(
                status = 409,
                error = "Conflict",
                message = exception.message ?: "User already exists",
            )
        return Response.status(Response.Status.CONFLICT).entity(errorResponse).build()
    }
}

/**
 * Exception handler for IllegalArgumentException (domain validation errors)
 */
@Provider
class IllegalArgumentExceptionMapper : ExceptionMapper<IllegalArgumentException> {
    override fun toResponse(exception: IllegalArgumentException): Response {
        val errorResponse =
            ErrorResponse(
                status = 400,
                error = "Bad Request",
                message = exception.message ?: "Invalid input",
            )
        return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build()
    }
}

/**
 * Exception handler for ConstraintViolationException (bean validation errors)
 */
@Provider
class ConstraintViolationExceptionMapper : ExceptionMapper<ConstraintViolationException> {
    override fun toResponse(exception: ConstraintViolationException): Response {
        val validationErrors =
            exception.constraintViolations.associate {
                it.propertyPath.toString() to it.message
            }

        val errorResponse =
            ErrorResponse(
                status = 400,
                error = "Bad Request",
                message = "Validation failed",
                validationErrors = validationErrors,
            )
        return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build()
    }
}

/**
 * Generic exception handler for unexpected errors
 */
@Provider
class GenericExceptionMapper : ExceptionMapper<Exception> {
    override fun toResponse(exception: Exception): Response {
        val errorResponse =
            ErrorResponse(
                status = 500,
                error = "Internal Server Error",
                message = "An unexpected error occurred",
            )
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build()
    }
}
