package guru.qa.rococo.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class AuthController {
    @GetMapping("/login")
    fun login(): String = "login"
}
