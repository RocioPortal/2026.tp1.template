package com.bibliotech.service;

import com.bibliotech.model.Categoria;
import com.bibliotech.model.Recurso;
import com.bibliotech.repository.Repository;
import com.bibliotech.model.Libro;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LibroService {

    private final Repository<Libro, String> libroRepository;

    // Inyección por constructor (SOLID - Dependency Inversion)
    public LibroService(Repository<Libro, String> libroRepository) {
        this.libroRepository = libroRepository;
    }

    public void registrarLibro(Libro libro) {
        libroRepository.guardar(libro);
    }

    public Optional<Libro> buscarPorIsbn(String isbn) {
        return libroRepository.buscarPorId(isbn);
    }

    public List<Libro> buscarPorTitulo(String titulo) {
        return libroRepository.buscarTodos().stream()
                .filter(l -> l.titulo().toLowerCase()
                        .contains(titulo.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Libro> buscarPorAutor(String autor) {
        return libroRepository.buscarTodos().stream()
                .filter(l -> l.autor().toLowerCase()
                        .contains(autor.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Libro> buscarPorCategoria(Categoria categoria) {
        return libroRepository.buscarTodos().stream()
                .filter(l -> l.categoria() == categoria)
                .collect(Collectors.toList());
    }

    public List<Libro> listarTodos() {
        return libroRepository.buscarTodos();
    }
}