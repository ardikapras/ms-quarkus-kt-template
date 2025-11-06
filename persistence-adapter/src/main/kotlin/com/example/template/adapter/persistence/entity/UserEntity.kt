package com.example.template.adapter.persistence.entity

import com.example.template.domain.model.*
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import jakarta.persistence.*
import java.time.Instant
import java.util.*

/**
 * JPA entity for User
 */
@Entity
@Table(
    name = "users",
    indexes = [
        Index(name = "idx_user_email", columnList = "email", unique = true)
    ]
)
class UserEntity : PanacheEntityBase {

    @Id
    @Column(name = "id", nullable = false)
    var id: UUID = UUID.randomUUID()

    @Column(name = "email", nullable = false, unique = true, length = 255)
    var email: String = ""

    @Column(name = "first_name", nullable = false, length = 50)
    var firstName: String = ""

    @Column(name = "last_name", nullable = false, length = 50)
    var lastName: String = ""

    @Column(name = "active", nullable = false)
    var active: Boolean = true

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now()

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()

    companion object {
        /**
         * Converts domain User to JPA entity
         */
        fun fromDomain(user: User): UserEntity {
            return UserEntity().apply {
                id = user.id.value
                email = user.email.value
                firstName = user.name.firstName
                lastName = user.name.lastName
                active = user.active
                createdAt = user.createdAt
                updatedAt = user.updatedAt
            }
        }
    }

    /**
     * Converts JPA entity to domain User
     */
    fun toDomain(): User {
        return User(
            id = UserId(id),
            email = Email(email),
            name = UserName(
                firstName = firstName,
                lastName = lastName
            ),
            active = active,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
