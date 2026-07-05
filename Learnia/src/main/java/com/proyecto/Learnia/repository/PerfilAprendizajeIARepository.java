package com.proyecto.Learnia.repository;

import com.proyecto.Learnia.entity.PerfilAprendizajeIA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PerfilAprendizajeIARepository extends JpaRepository<PerfilAprendizajeIA, Long> {

    Optional<PerfilAprendizajeIA> findByUsuario_IdUsuario(Long idUsuario);
}
