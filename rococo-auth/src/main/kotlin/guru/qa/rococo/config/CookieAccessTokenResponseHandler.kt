package guru.qa.rococo.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.http.MediaType
import org.springframework.http.server.ServletServerHttpResponse
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter
import java.time.Duration
import java.time.Instant

class CookieAccessTokenResponseHandler : AuthenticationSuccessHandler {
    private val writer = OAuth2AccessTokenResponseHttpMessageConverter()

    override fun onAuthenticationSuccess(req: HttpServletRequest, res: HttpServletResponse, auth: Authentication) {
        val at = auth as OAuth2AccessTokenAuthenticationToken

        val access = at.accessToken.tokenValue
        val expires = at.accessToken.expiresAt?.let { Duration.between(Instant.now(), it).seconds } ?: 0

        // 1) Кладём access_token в HttpOnly cookie (авто-уходит на 3000/8080/9000)
        val accessCookie = ResponseCookie.from("rococo_access", access)
            .httpOnly(true)
            .secure(false)              // для http на локалке; на https поставь true
            .path("/")
            .sameSite("Lax")            // порты не мешают, это same-site
            .maxAge(expires)
            .build()
        res.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString())

        // (опц.) можно положить id_token в НЕ HttpOnly, если когда-то пригодится фронту:
        (at.additionalParameters["id_token"] as? String)?.let { idt ->
            val idCookie = ResponseCookie.from("rococo_id", idt)
                .httpOnly(false).secure(false).path("/").sameSite("Lax").maxAge(expires).build()
            res.addHeader(HttpHeaders.SET_COOKIE, idCookie.toString())
        }

        // 2) Вернём стандартный JSON, как обычно
        val resp = OAuth2AccessTokenResponse.withToken(access)
            .tokenType(OAuth2AccessToken.TokenType.BEARER)
            .scopes(at.accessToken.scopes)
            .expiresIn(expires)
            .refreshToken(at.refreshToken?.tokenValue)
            .additionalParameters(at.additionalParameters)
            .build()

        res.contentType = MediaType.APPLICATION_JSON_VALUE
        writer.write(resp, MediaType.APPLICATION_JSON, ServletServerHttpResponse(res))
    }
}