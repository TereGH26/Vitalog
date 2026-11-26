package com.vitalog.service;

public class EmailAlreadyRegisteredException extends RuntimeException {

    public EmailAlreadyRegisteredException() {
        super("Este correo electrónico ya está registrado.");
    }
}