package guru.qa.rococo.controller

import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

data class SessionJson(
    val authenticated: Boolean,
    val username: String? = null
)

@RestController
class SessionController {

    @GetMapping("/api/session")
    fun session(auth: Authentication?): SessionJson {
        // Если запроса без токена — Authentication = null
        val jwt = auth?.principal as? Jwt ?: return SessionJson(authenticated = false)

        // Откуда взять имя:
        // - OpenID ID Token часто кладёт preferred_username
        // - Иначе sub, а в крайнем случае subject
        val username = (jwt.getClaimAsString("preferred_username"))
            ?: jwt.getClaimAsString("sub")
            ?: jwt.subject

        return SessionJson(authenticated = true, username = username)
    }
}