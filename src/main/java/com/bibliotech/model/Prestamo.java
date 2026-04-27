package com.bibliotech.model;

import java.time.LocalDate;

public record Prestamo(
        String id,
        Socio socio,
        Libro libro,
        LocalDate fechaPrestamo,
        LocalDate fechaDevolucionPlanificada,
        LocalDate fechaDevolucionReal
) {
    // Para saber si ya se devolvió
    public boolean esDevuelto() {
        return fechaDevolucionReal != null;
    }
}