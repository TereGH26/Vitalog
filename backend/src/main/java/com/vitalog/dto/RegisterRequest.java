package com.vitalog.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Ingresa un correo electrónico.")
        @Email(message = "Ingresa un correo electrónico válido.")
        @Size(max = 254, message = "El correo electrónico es demasiado largo.")
        String email,

        @NotBlank(message = "Ingresa una contraseña.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9\\s]).{8,}$",
                message = "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial.")
        String password) {
}