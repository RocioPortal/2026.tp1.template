package com.bibliotech.repository;

import com.bibliotech.model.Recurso;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RecursoRepositoryMemoria implements Repository<Recurso, String> {

    private final List<Recurso> recursos = new ArrayList<>();

    @Override
    public void guardar(Recurso recurso) {
        recursos.add(recurso);
    }

    @Override
    public Optional<Recurso> buscarPorId(String isbn) {
        return recursos.stream()
                .filter(r -> r.isbn().equals(isbn))
                .findFirst();
    }

    @Override
    public List<Recurso> buscarTodos() {
        return new ArrayList<>(recursos);
    }

    @Override
    public void actualizar(Recurso recurso, String isbn) {
        for (int i = 0; i < recursos.size(); i++) {
            if (recursos.get(i).isbn().equals(isbn)) {
                recursos.set(i, recurso);
                return;
            }
        }
    }
}