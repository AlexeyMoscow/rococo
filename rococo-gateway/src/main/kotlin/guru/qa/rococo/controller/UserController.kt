package guru.qa.rococo.controller

import guru.qa.rococo.dto.UserJson
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController {
    @GetMapping("/api/user")
    fun me(@AuthenticationPrincipal jwt: Jwt): UserJson {
        // возьми preferred_username если есть, иначе sub
        val username = jwt.claims["preferred_username"] as? String ?: jwt.subject
        return UserJson(username = username)
    }
}