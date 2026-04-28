package com.bibliotech.repository;

import com.bibliotech.model.Socio;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SocioRepositoryMemoria implements Repository<Socio, String> {
    private final List<Socio> socios = new ArrayList<>();

    @Override
    public void guardar(Socio socio) {
        socios.add(socio);
    }

    @Override
    public Optional<Socio> buscarPorId(String dni) {
        return socios.stream()
                .filter(s -> s.dni().equals(dni))
                .findFirst();
    }

    @Override
    public List<Socio> buscarTodos() {
        return new ArrayList<>(socios);
    }

    @Override
    public void actualizar(Socio socio, String dni) {
        for (int i = 0; i < socios.size(); i++) {
            if (socios.get(i).dni().equals(dni)) {
                socios.set(i, socio);
                return;
            }
        }
    }
}