package com.proyecto.Learnia.service;

import com.proyecto.Learnia.dto.PlanEstudioResumenDTO;
import com.proyecto.Learnia.dto.RecordatorioDTO;
import com.proyecto.Learnia.entity.PlanEstudio;
import com.proyecto.Learnia.entity.PlanEstudioDia;

import java.time.LocalDate;
import java.util.List;

public interface PlanEstudioService {

    PlanEstudio generarPlan(Long idUsuario, Long idCategoria, LocalDate fechaExamen, String titulo);

    List<PlanEstudioResumenDTO> obtenerPlanesUsuario(Long idUsuario);

    PlanEstudio obtenerPlan(Long idPlan, Long idUsuario);

    List<PlanEstudioDia> obtenerDias(Long idPlan);

    void marcarDiaCompletado(Long idDia, Long idUsuario, boolean completado);

    void eliminarPlan(Long idPlan, Long idUsuario);

    List<RecordatorioDTO> obtenerRecordatorios(Long idUsuario);
}