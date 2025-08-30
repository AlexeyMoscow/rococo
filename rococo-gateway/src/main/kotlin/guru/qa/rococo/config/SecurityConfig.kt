package guru.qa.rococo.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeHttpRequests { auth ->
            auth
                // /api/session — публичный GET
                .requestMatchers(HttpMethod.GET, "/api/session").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()
        }
        // Принимаем и валидируем JWT от auth-сервера
        http.oauth2ResourceServer { it.jwt(Customizer.withDefaults()) }
        // CSRF для API не нужен
        http.csrf { it.disable() }
        return http.build()
    }
}