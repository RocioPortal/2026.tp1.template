package com.bibliotech.model;

public record Libro(
        String isbn,
        String titulo,
        String autor,
        int año,
        String categoria,
        boolean esDigital //  Libros Físicos y E-books
) implements Recurso {}