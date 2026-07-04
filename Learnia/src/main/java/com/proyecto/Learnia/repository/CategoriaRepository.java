package com.proyecto.Learnia.repository;

import com.proyecto.Learnia.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    List<Categoria> findByActivaTrue();
    List<Categoria> findByActivaFalse();
    long countByActivaTrue();
}