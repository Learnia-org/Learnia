package com.proyecto.Learnia.repository;

import com.proyecto.Learnia.entity.IntentoEjercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntentoEjercicioRepository extends JpaRepository<IntentoEjercicio, Long> {

    List<IntentoEjercicio> findTop20ByUsuario_IdUsuarioOrderByFechaDesc(Long idUsuario);

    long countByUsuario_IdUsuario(Long idUsuario);

    long countByUsuario_IdUsuarioAndEsCorrectaTrue(Long idUsuario);

    long countByUsuario_IdUsuarioAndCategoria_IdCategoria(Long idUsuario, Long idCategoria);

    long countByUsuario_IdUsuarioAndCategoria_IdCategoriaAndEsCorrectaTrue(Long idUsuario, Long idCategoria);
}
