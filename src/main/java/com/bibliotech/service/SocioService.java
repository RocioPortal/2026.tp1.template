package com.bibliotech.service;

import com.bibliotech.model.Socio;
import com.bibliotech.repository.Repository;
import com.bibliotech.exception.DniDuplicadoException;
import com.bibliotech.exception.EmailInvalidoException;
import com.bibliotech.exception.LimitePrestamosExcedidoException;

public class SocioService {
    private final Repository<Socio, String> socioRepository;

    public SocioService(Repository<Socio, String> socioRepository) {
        this.socioRepository = socioRepository;
    }

    public void registrarSocio(Socio socio) {
        // 1. Validar Email
        if (!socio.email().contains("@")) {
            throw new EmailInvalidoException(socio.email());
        }

        // 2. Validar DNI único
        if (socioRepository.buscarPorId(socio.dni()).isPresent()) {
            throw new DniDuplicadoException(socio.dni());
        }

        socioRepository.guardar(socio);
    }
    public void validarCupoDisponible(Socio socio, int librosActuales) {
        if (librosActuales >= socio.tipo().getLimitePrestamos()) {
            throw new LimitePrestamosExcedidoException(socio.nombre(), socio.tipo().getLimitePrestamos());
        }
    }
}