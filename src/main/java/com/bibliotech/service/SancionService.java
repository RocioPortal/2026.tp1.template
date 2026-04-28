package com.bibliotech.service;

import com.bibliotech.exception.SocioSancionadoException;
import com.bibliotech.model.Sancion;
import com.bibliotech.model.Socio;
import com.bibliotech.repository.Repository;

import java.util.List;
import java.util.Optional;

public class SancionService {

    private final Repository<Sancion, String> sancionRepository;

    public SancionService(Repository<Sancion, String> sancionRepository) {
        this.sancionRepository = sancionRepository;
    }

    // Crea y guarda una sanción para un socio
    public void sancionarSocio(Socio socio, long diasRetraso) {
        Sancion sancion = Sancion.crear(socio, diasRetraso);
        sancionRepository.guardar(sancion);
    }

    // Lanza excepción si el socio tiene sanción activa
    public void verificarSancion(Socio socio) {
        Optional<Sancion> sancionActiva = sancionRepository.buscarTodos().stream()
                .filter(s -> s.socio().dni().equals(socio.dni()) && s.estaActiva())
                .findFirst();

        sancionActiva.ifPresent(s -> {
            throw new SocioSancionadoException(socio.nombre(), s.fechaFin());
        });
    }

    public List<Sancion> listarTodas() {
        return sancionRepository.buscarTodos();
    }

    public List<Sancion> listarActivas() {
        return sancionRepository.buscarTodos().stream()
                .filter(Sancion::estaActiva)
                .toList();
    }
}