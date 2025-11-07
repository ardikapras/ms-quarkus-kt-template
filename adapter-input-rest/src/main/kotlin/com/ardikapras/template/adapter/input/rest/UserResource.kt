package com.ardikapras.template.adapter.input.rest

import com.ardikapras.template.adapter.input.rest.dto.CreateUserRequest
import com.ardikapras.template.adapter.input.rest.dto.UpdateUserRequest
import com.ardikapras.template.application.dto.UserResponse
import com.ardikapras.template.application.usecase.CreateUserUseCase
import com.ardikapras.template.application.usecase.DeleteUserUseCase
import com.ardikapras.template.application.usecase.GetAllUsersUseCase
import com.ardikapras.template.application.usecase.GetUserUseCase
import com.ardikapras.template.application.usecase.UpdateUserUseCase
import jakarta.annotation.security.PermitAll
import jakarta.annotation.security.RolesAllowed
import jakarta.validation.Valid
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
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
@Tag(name = "User Management", description = "Operations for managing users")
class UserResource(
    private val createUserUseCase: CreateUserUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
) {
    @POST
    @RolesAllowed("user", "admin")
    @Operation(summary = "Create a new user", description = "Creates a new user with the provided information")
    @APIResponses(
        value = [
            APIResponse(
                responseCode = "201",
                description = "User created successfully",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON,
                        schema = Schema(implementation = UserResponse::class),
                    ),
                ],
            ),
            APIResponse(responseCode = "400", description = "Invalid input data"),
            APIResponse(responseCode = "409", description = "User with this email already exists"),
        ],
    )
    fun createUser(
        @Valid request: CreateUserRequest,
    ): Response {
        val user = createUserUseCase(request.toCommand())
        return Response.status(Response.Status.CREATED).entity(user).build()
    }

    @GET
    @RolesAllowed("user", "admin")
    @Operation(summary = "Get all users", description = "Retrieves a list of all users in the system")
    @APIResponse(
        responseCode = "200",
        description = "List of users retrieved successfully",
        content = [
            Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = Schema(implementation = Array<UserResponse>::class),
            ),
        ],
    )
    fun getAllUsers(): List<UserResponse> = getAllUsersUseCase()

    @GET
    @Path("/{id}")
    @PermitAll
    @Operation(summary = "Get user by ID", description = "Retrieves a specific user by their unique identifier")
    @APIResponses(
        value = [
            APIResponse(
                responseCode = "200",
                description = "User found",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON,
                        schema = Schema(implementation = UserResponse::class),
                    ),
                ],
            ),
            APIResponse(responseCode = "404", description = "User not found"),
        ],
    )
    fun getUserById(
        @Parameter(description = "User ID (UUID)", required = true)
        @PathParam("id")
        id: String,
    ): UserResponse = getUserUseCase(id)

    @PUT
    @Path("/{id}")
    @RolesAllowed("user", "admin")
    @Operation(summary = "Update user", description = "Updates an existing user's information")
    @APIResponses(
        value = [
            APIResponse(
                responseCode = "200",
                description = "User updated successfully",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON,
                        schema = Schema(implementation = UserResponse::class),
                    ),
                ],
            ),
            APIResponse(responseCode = "400", description = "Invalid input data"),
            APIResponse(responseCode = "404", description = "User not found"),
        ],
    )
    fun updateUser(
        @Parameter(description = "User ID (UUID)", required = true)
        @PathParam("id")
        id: String,
        @Valid request: UpdateUserRequest,
    ): UserResponse = updateUserUseCase(request.toCommand(id))

    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    @Operation(summary = "Delete user", description = "Deletes a user from the system (admin only)")
    @APIResponses(
        value = [
            APIResponse(responseCode = "204", description = "User deleted successfully"),
            APIResponse(responseCode = "404", description = "User not found"),
        ],
    )
    fun deleteUser(
        @Parameter(description = "User ID (UUID)", required = true)
        @PathParam("id")
        id: String,
    ): Response {
        deleteUserUseCase(id)
        return Response.noContent().build()
    }
}
