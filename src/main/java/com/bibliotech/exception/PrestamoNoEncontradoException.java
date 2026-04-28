package com.bibliotech.exception;

public class PrestamoNoEncontradoException extends BibliotecaException {
    public PrestamoNoEncontradoException(String id) {
        super("Prestamo con ID " + id + " no encontrado.");
    }
}