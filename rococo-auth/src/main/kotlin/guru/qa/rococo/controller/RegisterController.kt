package guru.qa.rococo.controller

import guru.qa.rococo.service.RegistrationModel
import guru.qa.rococo.service.UserService
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping

@Controller
class RegisterController(
    private val userService: UserService
) {
    @GetMapping("/register")
    fun registerPage(model: Model): String {
        if (!model.containsAttribute("registrationModel")) {
            model.addAttribute("registrationModel", RegistrationModel())
        }
        return "register"
    }

    @PostMapping("/register")
    fun register(
        @ModelAttribute("registrationModel") @Valid form: RegistrationModel,
        bindingResult: BindingResult,
        model: Model
    ): String {
        if (bindingResult.hasErrors()) return "register"
        if (form.password != form.passwordSubmit) {
            bindingResult.rejectValue("passwordSubmit", "registration.passwordSubmit.mismatch", "Пароли не совпадают")
            return "register"
        }
        return runCatching {
            userService.register(form.username, form.password)
            model.addAttribute("username", form.username)
            model.addAttribute("frontUri", "/login")
            "register" // показать блок «Добро пожаловать» на этой же странице
        }.getOrElse { ex ->
            bindingResult.rejectValue("username", "registration.username.exists", ex.message ?: "Ошибка регистрации")
            "register"
        }
    }
}
