package com.bibliotech.service;

import com.bibliotech.exception.BibliotecaException;
import com.bibliotech.exception.LibroNoDisponibleException;
import com.bibliotech.model.Prestamo;
import com.bibliotech.model.Recurso;
import com.bibliotech.model.Sancion;
import com.bibliotech.model.Socio;
import com.bibliotech.repository.Repository;
import com.bibliotech.exception.SocioNoEncontradoException;
import com.bibliotech.exception.PrestamoNoEncontradoException;
import java.util.List;

public class PrestamoService {

    private final Repository<Prestamo, String> prestamoRepository;
    private final SocioService socioService;
    private final RecursoService recursoService;
    private final SancionService sancionService;

    public PrestamoService(Repository<Prestamo, String> prestamoRepository,
                           SocioService socioService,
                           RecursoService recursoService,
                           SancionService sancionService) {
        this.prestamoRepository = prestamoRepository;
        this.socioService = socioService;
        this.recursoService = recursoService;
        this.sancionService = sancionService;
    }

    public void registrarPrestamo(String dni, String isbn) throws BibliotecaException {
        // 1. Buscar socio
        Socio socio = socioService.buscarPorDni(dni)
                .orElseThrow(() -> new SocioNoEncontradoException(dni));

        // 2. Verificar que no tenga sancion activa
        sancionService.verificarSancion(socio);

        // 3. Buscar recurso
        Recurso recurso = recursoService.buscarPorIsbn(isbn)
                .orElseThrow(() -> new LibroNoDisponibleException(isbn));

        // 4. Verificar disponibilidad
        boolean yaPrestado = prestamoRepository.buscarTodos().stream()
                .anyMatch(p -> p.recurso().isbn().equals(isbn) && !p.estaDevuelto());
        if (yaPrestado) {
            throw new LibroNoDisponibleException(isbn);
        }

        // 5. Verificar cupo del socio
        long prestamosActivos = prestamoRepository.buscarTodos().stream()
                .filter(p -> p.socio().dni().equals(dni) && !p.estaDevuelto())
                .count();
        socioService.validarCupoDisponible(socio, (int) prestamosActivos);

        // 6. Crear y guardar prestamo
        Prestamo prestamo = Prestamo.nuevo(socio, recurso);
        prestamoRepository.guardar(prestamo);
    }

    public long registrarDevolucion(String idPrestamo) throws BibliotecaException {
        // 1. Buscar prestamo
        Prestamo prestamo = prestamoRepository.buscarPorId(idPrestamo)
                .orElseThrow(() -> new PrestamoNoEncontradoException(idPrestamo));

        if (prestamo.estaDevuelto()) {
            throw new BibliotecaException("Este prestamo ya fue devuelto.");
        }

        // 2. Registrar devolucion
        Prestamo devuelto = prestamo.conDevolucion();
        prestamoRepository.actualizar(devuelto, idPrestamo);

        // 3. Si hubo retraso, sancionar al socio
        long diasRetraso = devuelto.calcularDiasRetraso();
        if (diasRetraso > 0) {
            sancionService.sancionarSocio(prestamo.socio(), diasRetraso);
        }

        return diasRetraso;
    }

    public List<Prestamo> obtenerHistorial() {
        return prestamoRepository.buscarTodos();
    }
}