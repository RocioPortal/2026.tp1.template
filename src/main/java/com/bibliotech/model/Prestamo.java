package com.bibliotech.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public record Prestamo(
        String id,
        Socio socio,
        Recurso recurso,
        LocalDate fechaInicio,
        LocalDate fechaLimite,
        Optional<LocalDate> fechaDevolucion
) {
    public static final int DIAS_PRESTAMO = 15;

    public boolean estaDevuelto() {
        return fechaDevolucion.isPresent();
    }

    public boolean estaVencido() {
        return !estaDevuelto() && LocalDate.now().isAfter(fechaLimite);
    }

    public long calcularDiasRetraso() {
        if (estaDevuelto()) {
            LocalDate devolucion = fechaDevolucion.get();
            if (devolucion.isAfter(fechaLimite)) {
                return ChronoUnit.DAYS.between(fechaLimite, devolucion);
            }
        } else if (estaVencido()) {
            return ChronoUnit.DAYS.between(fechaLimite, LocalDate.now());
        }
        return 0;
    }

    public static Prestamo nuevo(Socio socio, Recurso recurso) {
        LocalDate hoy = LocalDate.now();
        String id = "P-" + socio.dni() + "-" + recurso.isbn() + "-" + hoy;
        return new Prestamo(
                id, socio, recurso, hoy,
                hoy.plusDays(DIAS_PRESTAMO),
                Optional.empty()
        );
    }

    public Prestamo conDevolucion() {
        return new Prestamo(
                this.id, this.socio, this.recurso,
                this.fechaInicio, this.fechaLimite,
                Optional.of(LocalDate.now())
        );
    }
}