package com.proyecto.Learnia.service;

import com.proyecto.Learnia.entity.Categoria;
import com.proyecto.Learnia.exception.ResourceNotFoundException;
import com.proyecto.Learnia.exception.SuccesException;
import com.proyecto.Learnia.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoriaServiceImp implements CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Override
    public List<Categoria> listar() {
        return categoriaRepository.findAll();
    }

    @Override
    public Categoria crear(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    @Override
    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id).orElse(null);
    }

    @Override
    public Categoria actualizar(Long id, Categoria categoria) {
        Categoria existente = categoriaRepository.findById(id).orElse(null);
        if (existente != null) {
            existente.setNombreCategoria(categoria.getNombreCategoria());
            existente.setDescripcionCategoria(categoria.getDescripcionCategoria());
            return categoriaRepository.save(existente);
        }
        return null;
    }

    @Override
    public void eliminar(Long id) {
        if(!categoriaRepository.existsById(id)){
            throw new ResourceNotFoundException("La categoria con id: " + id + " no fue encontrado");
        }
        categoriaRepository.deleteById(id);
        throw new SuccesException("Categoria eliminada correctamente");
    }
}