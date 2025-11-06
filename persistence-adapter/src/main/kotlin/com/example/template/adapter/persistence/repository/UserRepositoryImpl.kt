package com.example.template.adapter.persistence.repository

import com.example.template.adapter.persistence.entity.UserEntity
import com.example.template.domain.model.Email
import com.example.template.domain.model.User
import com.example.template.domain.model.UserId
import com.example.template.domain.port.UserRepository
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import java.util.*

/**
 * JPA implementation of UserRepository
 */
@ApplicationScoped
class UserRepositoryImpl : PanacheRepositoryBase<UserEntity, UUID>, UserRepository {

    @Transactional
    override fun save(user: User): User {
        val entity = UserEntity.fromDomain(user)
        persist(entity)
        return entity.toDomain()
    }

    override fun findById(id: UserId): User? {
        return findById(id.value)?.toDomain()
    }

    override fun findByEmail(email: Email): User? {
        return find("email", email.value).firstResult()?.toDomain()
    }

    override fun findAll(): List<User> {
        return listAll().map { it.toDomain() }
    }

    @Transactional
    override fun deleteById(id: UserId) {
        delete("id", id.value)
    }

    override fun existsByEmail(email: Email): Boolean {
        return count("email", email.value) > 0
    }
}
