package com.bibliotech.exception;

public class LimitePrestamosExcedidoException extends BibliotecaException {
    public LimitePrestamosExcedidoException(String nombreSocio, int limite) {
        super("El socio " + nombreSocio + " no puede retirar más libros. Su límite es de " + limite + ".");
    }
}