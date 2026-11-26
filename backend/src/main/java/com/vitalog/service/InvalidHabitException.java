package com.vitalog.service;

public class InvalidHabitException extends RuntimeException {
    public InvalidHabitException(String message) {
        super(message);
    }
}
