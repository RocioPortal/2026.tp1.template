package com.bibliotech.model;

import java.time.LocalDate;

public record Sancion(
        String id,
        Socio socio,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        long diasRetraso
) {
    public boolean estaActiva() {
        return LocalDate.now().isBefore(fechaFin) || LocalDate.now().isEqual(fechaFin);
    }

    public static Sancion crear(Socio socio, long diasRetraso) {
        LocalDate hoy = LocalDate.now();
        // El bloqueo dura el doble de los días de retraso
        LocalDate fin = hoy.plusDays(diasRetraso * 2);
        String id = "S-" + socio.dni() + "-" + hoy;
        return new Sancion(id, socio, hoy, fin, diasRetraso);
    }
}