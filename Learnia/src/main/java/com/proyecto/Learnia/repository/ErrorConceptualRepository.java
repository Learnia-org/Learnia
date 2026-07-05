package com.proyecto.Learnia.repository;

import com.proyecto.Learnia.entity.ErrorConceptual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ErrorConceptualRepository extends JpaRepository<ErrorConceptual, Long> {

    List<ErrorConceptual> findByUsuario_IdUsuarioOrderByFechaDeteccionDesc(Long idUsuario);

    List<ErrorConceptual> findTop10ByUsuario_IdUsuarioOrderByFechaDeteccionDesc(Long idUsuario);

    List<ErrorConceptual> findByUsuario_IdUsuarioAndResueltoFalseOrderByFechaDeteccionDesc(Long idUsuario);

    long countByUsuario_IdUsuarioAndCategoria_IdCategoriaAndResueltoFalse(Long idUsuario, Long idCategoria);

    long countByUsuario_IdUsuario(Long idUsuario);
}
