package com.bibliotech.service;

import com.bibliotech.exception.BibliotecaException;
import com.bibliotech.exception.LibroNoDisponibleException;
import com.bibliotech.model.Prestamo;
import com.bibliotech.model.Recurso;
import com.bibliotech.model.Socio;
import com.bibliotech.repository.Repository;

import java.util.List;

public class PrestamoService {

    private final Repository<Prestamo, String> prestamoRepository;
    private final SocioService socioService;
    private final RecursoService recursoService;

    public PrestamoService(Repository<Prestamo, String> prestamoRepository,
                           SocioService socioService,
                           RecursoService recursoService) {
        this.prestamoRepository = prestamoRepository;
        this.socioService = socioService;
        this.recursoService = recursoService;
    }

    public void registrarPrestamo(String dni, String isbn) throws BibliotecaException {
        // 1. Buscar socio
        Socio socio = socioService.buscarPorDni(dni)
                .orElseThrow(() -> new BibliotecaException(
                        "Member with DNI " + dni + " not found."));

        // 2. Buscar recurso (puede ser Libro o Ebook)
        Recurso recurso = recursoService.buscarPorIsbn(isbn)
                .orElseThrow(() -> new LibroNoDisponibleException(isbn));

        // 3. Verificar que no esté ya prestado
        boolean yaPrestado = prestamoRepository.buscarTodos().stream()
                .anyMatch(p -> p.recurso().isbn().equals(isbn) && !p.estaDevuelto());
        if (yaPrestado) {
            throw new LibroNoDisponibleException(isbn);
        }

        // 4. Verificar cupo del socio
        long prestamosActivos = prestamoRepository.buscarTodos().stream()
                .filter(p -> p.socio().dni().equals(dni) && !p.estaDevuelto())
                .count();
        socioService.validarCupoDisponible(socio, (int) prestamosActivos);

        // 5. Crear y guardar préstamo
        Prestamo prestamo = Prestamo.nuevo(socio, recurso);
        prestamoRepository.guardar(prestamo);
    }

    public long registrarDevolucion(String idPrestamo) throws BibliotecaException {
        Prestamo prestamo = prestamoRepository.buscarPorId(idPrestamo)
                .orElseThrow(() -> new BibliotecaException(
                        "Prestamo " + idPrestamo + " no encontrado."));

        if (prestamo.estaDevuelto()) {
            throw new BibliotecaException("Este prestamo ya fue devuelto.");
        }

        Prestamo devuelto = prestamo.conDevolucion();
        prestamoRepository.actualizar(devuelto, idPrestamo); // reemplaza en lugar de agregar

        return devuelto.calcularDiasRetraso();
    }

    public List<Prestamo> obtenerHistorial() {
        return prestamoRepository.buscarTodos();
    }
}