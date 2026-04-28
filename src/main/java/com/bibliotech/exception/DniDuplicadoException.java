package com.bibliotech.exception;

public class DniDuplicadoException extends BibliotecaException {
    public DniDuplicadoException(String dni) {
        super("El socio con DNI " + dni + " ya se encuentra registrado.");
    }
}