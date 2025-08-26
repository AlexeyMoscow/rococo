package guru.qa.rococo.service

import guru.qa.rococo.data.AuthorityEntity
import guru.qa.rococo.data.UserEntity
import guru.qa.rococo.data.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    @Transactional
    fun register(username: String, rawPassword: String): UserEntity {
        require(!userRepository.existsByUsername(username)) { "Username already exists" }
        val user = UserEntity(username = username, password = passwordEncoder.encode(rawPassword))
        val role = AuthorityEntity(user = user, authority = "ROLE_USER")
        user.authorities.add(role)
        return userRepository.save(user)
    }
}