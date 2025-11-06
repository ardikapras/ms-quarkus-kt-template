package com.example.template.adapter.rest.resource

import com.example.template.adapter.rest.dto.CreateUserRequest
import com.example.template.adapter.rest.dto.UpdateUserRequest
import com.example.template.application.dto.UserResponse
import com.example.template.application.usecase.*
import jakarta.annotation.security.PermitAll
import jakarta.annotation.security.RolesAllowed
import jakarta.inject.Inject
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses
import org.eclipse.microprofile.openapi.annotations.tags.Tag

/**
 * REST resource for User operations
 */
@Path("/api/v1/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Users", description = "User management endpoints")
class UserResource @Inject constructor(
    private val createUserUseCase: CreateUserUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase
) {

    @POST
    @PermitAll
    @Operation(summary = "Create a new user", description = "Creates a new user with the provided information")
    @APIResponses(
        APIResponse(responseCode = "201", description = "User created successfully"),
        APIResponse(responseCode = "400", description = "Invalid request data"),
        APIResponse(responseCode = "409", description = "User already exists")
    )
    fun createUser(@Valid request: CreateUserRequest): Response {
        val user = createUserUseCase.execute(request.toCommand())
        return Response.status(Response.Status.CREATED).entity(user).build()
    }

    @GET
    @RolesAllowed("user", "admin")
    @Operation(summary = "Get all users", description = "Retrieves a list of all users")
    @APIResponse(responseCode = "200", description = "List of users retrieved successfully")
    fun getAllUsers(): List<UserResponse> {
        return getAllUsersUseCase.execute()
    }

    @GET
    @Path("/{id}")
    @RolesAllowed("user", "admin")
    @Operation(summary = "Get user by ID", description = "Retrieves a specific user by their ID")
    @APIResponses(
        APIResponse(responseCode = "200", description = "User found"),
        APIResponse(responseCode = "404", description = "User not found")
    )
    fun getUserById(
        @Parameter(description = "User ID", required = true)
        @PathParam("id") id: String
    ): UserResponse {
        return getUserUseCase.execute(id)
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed("user", "admin")
    @Operation(summary = "Update user", description = "Updates an existing user's information")
    @APIResponses(
        APIResponse(responseCode = "200", description = "User updated successfully"),
        APIResponse(responseCode = "404", description = "User not found"),
        APIResponse(responseCode = "400", description = "Invalid request data")
    )
    fun updateUser(
        @Parameter(description = "User ID", required = true)
        @PathParam("id") id: String,
        @Valid request: UpdateUserRequest
    ): UserResponse {
        return updateUserUseCase.execute(request.toCommand(id))
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    @Operation(summary = "Delete user", description = "Deletes a user by their ID")
    @APIResponses(
        APIResponse(responseCode = "204", description = "User deleted successfully"),
        APIResponse(responseCode = "404", description = "User not found")
    )
    fun deleteUser(
        @Parameter(description = "User ID", required = true)
        @PathParam("id") id: String
    ): Response {
        deleteUserUseCase.execute(id)
        return Response.noContent().build()
    }
}
