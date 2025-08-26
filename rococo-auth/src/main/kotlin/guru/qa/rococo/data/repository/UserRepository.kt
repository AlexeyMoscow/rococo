package guru.qa.rococo.data.repository

import guru.qa.rococo.data.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface UserRepository : JpaRepository<UserEntity, UUID> {
    fun findByUsername(username: String): Optional<UserEntity>
    fun existsByUsername(username: String): Boolean
}