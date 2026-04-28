package com.bibliotech.repository;

import com.bibliotech.model.Prestamo;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PrestamoRepositoryMemoria implements Repository<Prestamo, String> {

    private final List<Prestamo> prestamos = new ArrayList<>();

    @Override
    public void guardar(Prestamo prestamo) {
        prestamos.add(prestamo);
    }

    @Override
    public void actualizar(Prestamo prestamo, String id) {
        for (int i = 0; i < prestamos.size(); i++) {
            if (prestamos.get(i).id().equals(id)) {
                prestamos.set(i, prestamo);
                return;
            }
        }
    }

    @Override
    public Optional<Prestamo> buscarPorId(String id) {
        return prestamos.stream()
                .filter(p -> p.id().equals(id))
                .findFirst();
    }

    @Override
    public List<Prestamo> buscarTodos() {
        return new ArrayList<>(prestamos);
    }
}