package guru.qa.rococo.config

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.time.Duration
import java.util.*

@Configuration
class AuthorizationServerConfig {

    @Bean
    fun authorizationServerSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        val asConfigurer = OAuth2AuthorizationServerConfigurer.authorizationServer()
        val endpoints = asConfigurer.endpointsMatcher

        return http

            .securityMatcher(endpoints)

            .with(asConfigurer) { it.oidc(Customizer.withDefaults()) }

            .exceptionHandling { it.authenticationEntryPoint(LoginUrlAuthenticationEntryPoint("/login")) }

            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt(Customizer.withDefaults())
            }
            .build()
    }

    @Bean
    fun authorizationServerSettings(): AuthorizationServerSettings =
        AuthorizationServerSettings.builder().build()

    @Bean
    fun registeredClientRepository(passwordEncoder: org.springframework.security.crypto.password.PasswordEncoder): RegisteredClientRepository {
        val registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("rococo-client")
            .clientSecret(passwordEncoder.encode("secret"))
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .redirectUri("https://oauth.pstmn.io/v1/callback")
            .redirectUri("http://127.0.0.1:8080/login/oauth2/code/rococo-client")
            .scope("openid")
            .scope("profile")
            .scope("read")
            .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
            .tokenSettings(
                TokenSettings.builder()
                    .accessTokenTimeToLive(Duration.ofMinutes(30))
                    .refreshTokenTimeToLive(Duration.ofDays(30))
                    .reuseRefreshTokens(false)
                    .build()
            )
            .build()

        return InMemoryRegisteredClientRepository(registeredClient)
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
