package com.bibliotech.exception;

public class LibroNoDisponibleException extends BibliotecaException {
    public LibroNoDisponibleException(String isbn) {
        super("El recurso con ISBN " + isbn + " no está disponible para préstamo.");
    }
}