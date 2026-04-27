package com.bibliotech.exception;

public class DniDuplicadoException extends RuntimeException {
    public DniDuplicadoException(String dni) {
        super("El socio con DNI " + dni + " ya se encuentra registrado.");
    }
}