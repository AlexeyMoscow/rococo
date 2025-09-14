package guru.qa.rococo.config

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.core.oidc.OidcScopes
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.util.*

@Configuration
class AuthorizationServerConfig {

    @Bean
    @Order(1)
    fun authorizationServerSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http)
        http.getConfigurer(OAuth2AuthorizationServerConfigurer::class.java).oidc { }
        http.cors { } // CORS для /oauth2/** и /.well-known/**
        http.exceptionHandling { it.authenticationEntryPoint(LoginUrlAuthenticationEntryPoint("/login")) }
        return http.build()
    }

    @Bean
    fun authorizationServerSettings(): AuthorizationServerSettings =
        AuthorizationServerSettings.builder().issuer("http://127.0.0.1:9000").build()

    @Bean
    fun registeredClientRepository(pe: PasswordEncoder): RegisteredClientRepository {
        val spa = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("client")
            .clientAuthenticationMethods { m -> m.clear(); m.add(ClientAuthenticationMethod.NONE) } // public SPA
            .authorizationGrantTypes { g -> g.clear(); g.add(AuthorizationGrantType.AUTHORIZATION_CODE) }
            .redirectUri("http://127.0.0.1:3000/authorized")   // добавь РОВНО тот, что в URL
            .postLogoutRedirectUri("http://127.0.0.1:3000")
            .scope(OidcScopes.OPENID)
            .scope("profile")
            .clientSettings(ClientSettings.builder().requireProofKey(true).build()) // PKCE
            .build()

        val postman = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("rococo-client")
            .clientSecret(pe.encode("secret"))
            .clientAuthenticationMethods { m -> m.clear(); m.add(ClientAuthenticationMethod.CLIENT_SECRET_BASIC) }
            .authorizationGrantTypes { g -> g.clear(); g.add(AuthorizationGrantType.AUTHORIZATION_CODE); g.add(AuthorizationGrantType.REFRESH_TOKEN) }
            .redirectUri("https://oauth.pstmn.io/v1/callback")
            .scope(OidcScopes.OPENID).scope("profile")
            .build()

        return InMemoryRegisteredClientRepository(listOf(spa, postman))
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val c = CorsConfiguration().apply {
            allowedOrigins = listOf("http://127.0.0.1:3000", "http://localhost:3000")
            allowedMethods = listOf("GET","POST","OPTIONS")
            allowedHeaders = listOf("*")
            allowCredentials = true
        }
        return UrlBasedCorsConfigurationSource().apply { registerCorsConfiguration("/**", c) }
    }

    @Bean
    fun jwkSource(): JWKSource<SecurityContext> {
        val keyPair = generateRsaKey()
        val publicKey = keyPair.public as java.security.interfaces.RSAPublicKey
        val privateKey = keyPair.private as java.security.interfaces.RSAPrivateKey
        val rsaKey = RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .keyID(UUID.randomUUID().toString())
            .build()
        val jwkSet = JWKSet(rsaKey)
        return JWKSource { jwkSelector, _ -> jwkSelector.select(jwkSet) }
    }

    private fun generateRsaKey(): KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(2048)
        return keyPairGenerator.generateKeyPair()
    }
}
