package guru.qa.rococo.service

import jakarta.validation.constraints.NotBlank

data class RegistrationModel(
    @field:NotBlank(message = "Введите имя пользователя")
    var username: String = "",
    @field:NotBlank(message = "Введите пароль")
    var password: String = "",
    @field:NotBlank(message = "Повторите пароль")
    var passwordSubmit: String = ""
)
