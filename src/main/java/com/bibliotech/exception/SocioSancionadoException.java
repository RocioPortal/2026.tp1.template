package com.bibliotech.exception;

import java.time.LocalDate;

public class SocioSancionadoException extends BibliotecaException {
    public SocioSancionadoException(String nombreSocio, LocalDate fechaFin) {
        super("El socio " + nombreSocio + " tiene una sancion activa hasta el " + fechaFin + ".");
    }
}