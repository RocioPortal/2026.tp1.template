package com.bibliotech.service;

import com.bibliotech.model.Categoria;
import com.bibliotech.model.Recurso;
import com.bibliotech.repository.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RecursoService {

    private final Repository<Recurso, String> recursoRepository;

    public RecursoService(Repository<Recurso, String> recursoRepository) {
        this.recursoRepository = recursoRepository;
    }

    public void registrar(Recurso recurso) {
        recursoRepository.guardar(recurso);
    }

    public Optional<Recurso> buscarPorIsbn(String isbn) {
        return recursoRepository.buscarPorId(isbn);
    }

    public List<Recurso> buscarPorTitulo(String titulo) {
        return recursoRepository.buscarTodos().stream()
                .filter(r -> r.titulo().toLowerCase().contains(titulo.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Recurso> buscarPorAutor(String autor) {
        return recursoRepository.buscarTodos().stream()
                .filter(r -> r.autor().toLowerCase().contains(autor.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Recurso> buscarPorCategoria(Categoria categoria) {
        return recursoRepository.buscarTodos().stream()
                .filter(r -> r.categoria() == categoria)
                .collect(Collectors.toList());
    }

    public List<Recurso> listarTodos() {
        return recursoRepository.buscarTodos();
    }
}