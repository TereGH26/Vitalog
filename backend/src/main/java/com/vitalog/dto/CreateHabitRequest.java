package com.vitalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;

import java.util.Set;

public record CreateHabitRequest(
        @NotBlank(message = "Ingresa el nombre del hábito.")
        @Size(max = 100, message = "El nombre del hábito debe tener 100 caracteres como máximo.")
        String name,

        @Min(value = 1, message = "La duración debe ser de al menos 1 día.")
        Integer durationDays,

        @NotBlank(message = "Selecciona una frecuencia.")
        String frequency,

        Set<String> weekdays) {

    public Integer effectiveDurationDays() {
        return durationDays == null ? 66 : durationDays;
    }
}
