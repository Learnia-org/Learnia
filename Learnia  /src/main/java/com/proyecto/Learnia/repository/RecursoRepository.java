package com.proyecto.Learnia.repository;

import com.proyecto.Learnia.entity.Recurso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecursoRepository extends JpaRepository<Recurso, Long> {

    List<Recurso> findByCategoria_IdCategoria(Long idCategoria);
}
