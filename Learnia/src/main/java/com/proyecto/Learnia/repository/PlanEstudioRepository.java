package com.proyecto.Learnia.repository;

import com.proyecto.Learnia.entity.PlanEstudio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanEstudioRepository extends JpaRepository<PlanEstudio, Long> {

    List<PlanEstudio> findByUsuario_IdUsuarioOrderByFechaCreacionDesc(Long idUsuario);

    List<PlanEstudio> findByUsuario_IdUsuarioAndActivoTrue(Long idUsuario);
}