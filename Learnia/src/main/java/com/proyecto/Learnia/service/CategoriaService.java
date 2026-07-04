package com.proyecto.Learnia.service;

import com.proyecto.Learnia.entity.Categoria;
import java.util.List;

public interface CategoriaService {
    List<Categoria> listar();
    Categoria crear(Categoria categoria);
    Categoria actualizar(Long id, Categoria categoria);
    Categoria buscarPorId(Long id);
    void eliminar(Long id);
}