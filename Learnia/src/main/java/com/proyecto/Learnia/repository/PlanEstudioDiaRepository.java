package com.proyecto.Learnia.repository;

import com.proyecto.Learnia.entity.PlanEstudioDia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PlanEstudioDiaRepository extends JpaRepository<PlanEstudioDia, Long> {

    List<PlanEstudioDia> findByPlanEstudio_IdPlanOrderByNumeroDiaAscIdDiaAsc(Long idPlan);

    List<PlanEstudioDia> findByPlanEstudio_Usuario_IdUsuarioAndFechaLessThanEqualAndCompletadoFalseAndPlanEstudio_ActivoTrueOrderByFechaAsc(
            Long idUsuario, LocalDate fecha);

    long countByPlanEstudio_IdPlan(Long idPlan);

    long countByPlanEstudio_IdPlanAndCompletadoTrue(Long idPlan);
}