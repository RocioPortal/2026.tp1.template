package com.bibliotech.exception;

public class EmailInvalidoException extends BibliotecaException {
    public EmailInvalidoException(String email) {
        super("El formato del email '" + email + "' no es válido.");
    }
}