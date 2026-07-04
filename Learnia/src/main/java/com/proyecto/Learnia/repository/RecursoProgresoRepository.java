package com.proyecto.Learnia.repository;

import com.proyecto.Learnia.entity.RecursoProgreso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecursoProgresoRepository extends JpaRepository<RecursoProgreso, Long> {

    Optional<RecursoProgreso> findByUsuario_IdUsuarioAndRecurso_IdRecurso(Long idUsuario, Long idRecurso);

    List<RecursoProgreso> findByUsuario_IdUsuarioAndRecurso_Categoria_IdCategoria(Long idUsuario, Long idCategoria);

    List<RecursoProgreso> findByUsuario_IdUsuario(Long idUsuario);

    long countByUsuario_IdUsuarioAndRecurso_Categoria_IdCategoriaAndCompletadoTrue(Long idUsuario, Long idCategoria);
}