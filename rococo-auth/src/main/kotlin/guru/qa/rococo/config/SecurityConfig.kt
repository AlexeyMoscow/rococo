package guru.qa.rococo.config

import guru.qa.rococo.data.repository.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val userRepository: UserRepository
) {
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun userDetailsService(): UserDetailsService = UserDetailsService { username ->
        val user = userRepository.findByUsername(username)
            .orElseThrow { UsernameNotFoundException("User not found: $username") }
        val authorities = user.authorities.map { SimpleGrantedAuthority(it.authority) }
        User(user.username, user.password, user.enabled, true, true, true, authorities)
    }

    @Bean
    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/login", "/register", "/css/**", "/js/**", "/styles/**", "/images/**", "/favicon.ico").permitAll()
                    .anyRequest().authenticated()
            }
            .formLogin { it.loginPage("/login").permitAll() }
            .logout { it.logoutSuccessUrl("/login?logout").permitAll() }
            .csrf { it.ignoringRequestMatchers("/register") } // login остаётся с CSRF — токен в шаблоне
        return http.build()
    }
}
