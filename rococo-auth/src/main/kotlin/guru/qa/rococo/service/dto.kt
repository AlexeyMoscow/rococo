package guru.qa.rococo.service

import jakarta.validation.constraints.NotBlank

data class RegistrationRequest(
    @field:NotBlank val username: String,
    @field:NotBlank val password: String,
    @field:NotBlank val confirmPassword: String
)