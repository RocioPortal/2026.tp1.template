package com.bibliotech.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T, ID> {
    void guardar(T entidad);
    void actualizar(T entidad, ID id);
    Optional<T> buscarPorId(ID id);
    List<T> buscarTodos();
}