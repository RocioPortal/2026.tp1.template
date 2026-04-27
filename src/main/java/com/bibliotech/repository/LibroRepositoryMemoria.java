package com.bibliotech.repository;

import com.bibliotech.model.Libro;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LibroRepositoryMemoria implements Repository<Libro, String> {
    private final List<Libro> libros = new ArrayList<>();

    @Override
    public void guardar(Libro libro) {
        libros.add(libro);
    }

    @Override
    public Optional<Libro> buscarPorId(String isbn) {
        return libros.stream()
                .filter(l -> l.isbn().equals(isbn))
                .findFirst();
    }

    @Override
    public List<Libro> buscarTodos() {
        return new ArrayList<>(libros);
    }
}
