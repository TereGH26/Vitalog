package com.vitalog.controller;

import com.vitalog.service.EmailAlreadyRegisteredException;
import com.vitalog.service.InvalidHabitException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    ResponseEntity<Map<String, String>> handleDuplicateEmail() {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", "Ya existe una cuenta con ese correo electrónico."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, String>> handleInvalidRequest(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("Revisa los datos ingresados.");
        return ResponseEntity.badRequest().body(Map.of("message", message));
    }

    @ExceptionHandler(DataAccessException.class)
    ResponseEntity<Map<String, String>> handleDatabaseUnavailable() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "message",
                        "MongoDB rechazó la operación. Configura MONGODB_URI en Vitalog/.env con las credenciales del contenedor y reinicia VitaLog."));
    }

    @ExceptionHandler(InvalidHabitException.class)
    ResponseEntity<Map<String, String>> handleInvalidHabit(InvalidHabitException exception) {
        return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
    }
}