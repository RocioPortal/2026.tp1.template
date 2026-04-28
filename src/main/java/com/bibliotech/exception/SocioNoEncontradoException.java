package com.bibliotech.exception;

public class SocioNoEncontradoException extends BibliotecaException {
    public SocioNoEncontradoException(String dni) {
        super("Socio con DNI " + dni + " no encontrado.");
    }
}