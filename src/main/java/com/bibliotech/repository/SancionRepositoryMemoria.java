package com.bibliotech.repository;

import com.bibliotech.model.Sancion;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SancionRepositoryMemoria implements Repository<Sancion, String> {

    private final List<Sancion> sanciones = new ArrayList<>();

    @Override
    public void guardar(Sancion sancion) {
        sanciones.add(sancion);
    }

    @Override
    public void actualizar(Sancion sancion, String id) {
        for (int i = 0; i < sanciones.size(); i++) {
            if (sanciones.get(i).id().equals(id)) {
                sanciones.set(i, sancion);
                return;
            }
        }
    }

    @Override
    public Optional<Sancion> buscarPorId(String id) {
        return sanciones.stream()
                .filter(s -> s.id().equals(id))
                .findFirst();
    }

    @Override
    public List<Sancion> buscarTodos() {
        return new ArrayList<>(sanciones);
    }
}