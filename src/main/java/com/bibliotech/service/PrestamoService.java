package com.bibliotech.service;

import com.bibliotech.model.Prestamo;
import com.bibliotech.model.Socio;
import com.bibliotech.model.Libro;
import com.bibliotech.repository.Repository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class PrestamoService {
    private final Repository<Prestamo, String> prestamoRepository;
    private final SocioService socioService;

    public PrestamoService(Repository<Prestamo, String> prestamoRepository, SocioService socioService) {
        this.prestamoRepository = prestamoRepository;
        this.socioService = socioService;
    }

    public void registrarPrestamo(String id, Socio socio, Libro libro) {
        // 1. Validar si el socio puede llevar más libros
        long prestamosActivos = prestamoRepository.buscarTodos().stream()
                .filter(p -> p.socio().dni().equals(socio.dni()) && !p.esDevuelto())
                .count();

        socioService.validarCupoDisponible(socio, (int) prestamosActivos);

        // 2. Crear el préstamo (por defecto a 7 días)
        Prestamo nuevoPrestamo = new Prestamo(
                id, socio, libro, LocalDate.now(), LocalDate.now().plusDays(7), null
        );

        prestamoRepository.guardar(nuevoPrestamo);
    }

    public long calcularDiasRetraso(Prestamo prestamo) {
        if (prestamo.fechaDevolucionReal() == null) {
            // Si no se devolvió, comparamos con la fecha de hoy
            if (LocalDate.now().isAfter(prestamo.fechaDevolucionPlanificada())) {
                return ChronoUnit.DAYS.between(prestamo.fechaDevolucionPlanificada(), LocalDate.now());
            }
        } else {
            // Si ya se devolvió, comparamos la fecha real vs planificada
            if (prestamo.fechaDevolucionReal().isAfter(prestamo.fechaDevolucionPlanificada())) {
                return ChronoUnit.DAYS.between(prestamo.fechaDevolucionPlanificada(), prestamo.fechaDevolucionReal());
            }
        }
        return 0;
    }
}