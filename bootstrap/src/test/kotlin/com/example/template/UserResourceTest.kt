package com.example.template

import io.quarkus.test.junit.QuarkusTest
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class UserResourceTest {

    companion object {
        private var createdUserId: String? = null
    }

    @Test
    @Order(1)
    fun testCreateUser() {
        val requestBody = """
            {
                "email": "test@example.com",
                "firstName": "John",
                "lastName": "Doe"
            }
        """.trimIndent()

        val response = Given {
            contentType(ContentType.JSON)
            body(requestBody)
        } When {
            post("/api/v1/users")
        } Then {
            statusCode(201)
            body("email", equalTo("test@example.com"))
            body("firstName", equalTo("John"))
            body("lastName", equalTo("Doe"))
            body("fullName", equalTo("John Doe"))
            body("active", equalTo(true))
            body("id", notNullValue())
        }

        createdUserId = response.extract().path("id")
    }

    @Test
    @Order(2)
    fun testCreateUserWithDuplicateEmail() {
        val requestBody = """
            {
                "email": "test@example.com",
                "firstName": "Jane",
                "lastName": "Doe"
            }
        """.trimIndent()

        Given {
            contentType(ContentType.JSON)
            body(requestBody)
        } When {
            post("/api/v1/users")
        } Then {
            statusCode(409)
            body("error", equalTo("Conflict"))
        }
    }

    @Test
    @Order(3)
    fun testGetUserById() {
        Given {
            pathParam("id", createdUserId)
        } When {
            get("/api/v1/users/{id}")
        } Then {
            statusCode(200)
            body("id", equalTo(createdUserId))
            body("email", equalTo("test@example.com"))
        }
    }

    @Test
    @Order(4)
    fun testGetAllUsers() {
        When {
            get("/api/v1/users")
        } Then {
            statusCode(200)
            body("$.size()", greaterThan(0))
        }
    }

    @Test
    @Order(5)
    fun testUpdateUser() {
        val requestBody = """
            {
                "firstName": "Jane",
                "lastName": "Smith"
            }
        """.trimIndent()

        Given {
            pathParam("id", createdUserId)
            contentType(ContentType.JSON)
            body(requestBody)
        } When {
            put("/api/v1/users/{id}")
        } Then {
            statusCode(200)
            body("firstName", equalTo("Jane"))
            body("lastName", equalTo("Smith"))
            body("fullName", equalTo("Jane Smith"))
        }
    }

    @Test
    @Order(6)
    fun testDeleteUser() {
        Given {
            pathParam("id", createdUserId)
        } When {
            delete("/api/v1/users/{id}")
        } Then {
            statusCode(204)
        }
    }

    @Test
    @Order(7)
    fun testGetUserByIdNotFound() {
        Given {
            pathParam("id", createdUserId)
        } When {
            get("/api/v1/users/{id}")
        } Then {
            statusCode(404)
            body("error", equalTo("Not Found"))
        }
    }

    @Test
    fun testCreateUserWithInvalidEmail() {
        val requestBody = """
            {
                "email": "invalid-email",
                "firstName": "John",
                "lastName": "Doe"
            }
        """.trimIndent()

        Given {
            contentType(ContentType.JSON)
            body(requestBody)
        } When {
            post("/api/v1/users")
        } Then {
            statusCode(400)
        }
    }

    @Test
    fun testHealthCheck() {
        When {
            get("/health/ready")
        } Then {
            statusCode(200)
        }
    }

    @Test
    fun testMetricsEndpoint() {
        When {
            get("/metrics")
        } Then {
            statusCode(200)
        }
    }
}
