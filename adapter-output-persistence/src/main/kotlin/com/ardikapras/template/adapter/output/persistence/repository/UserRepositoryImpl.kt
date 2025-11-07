package com.ardikapras.template.adapter.output.persistence.repository

import com.ardikapras.template.adapter.output.persistence.entity.UserEntity
import com.ardikapras.template.domain.model.Email
import com.ardikapras.template.domain.model.User
import com.ardikapras.template.domain.model.UserId
import com.ardikapras.template.domain.port.output.UserRepository
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import java.util.UUID

/**
 * JPA implementation of UserRepository
 */
@ApplicationScoped
class UserRepositoryImpl :
    PanacheRepositoryBase<UserEntity, UUID>,
    UserRepository {
    @Transactional
    override fun save(user: User): User {
        val entity = UserEntity.fromDomain(user)
        // Check if entity already exists to decide between insert or update
        val existingEntity = findById(user.id.value)
        if (existingEntity != null) {
            // Entity exists, use merge to update
            getEntityManager().merge(entity)
        } else {
            // New entity, use persist
            persist(entity)
        }
        return entity.toDomain()
    }

    override fun findById(id: UserId): User? = findById(id.value)?.toDomain()

    override fun findByEmail(email: Email): User? = find("email", email.value).firstResult()?.toDomain()

    override fun getAllUsers(): List<User> = listAll().map { it.toDomain() }

    @Transactional
    override fun deleteById(id: UserId) {
        delete("id", id.value)
    }

    override fun existsByEmail(email: Email): Boolean = count("email", email.value) > 0
}
