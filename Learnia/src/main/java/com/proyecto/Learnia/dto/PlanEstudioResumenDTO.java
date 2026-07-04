package com.proyecto.Learnia.dto;

import com.proyecto.Learnia.entity.PlanEstudio;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PlanEstudioResumenDTO {
    private PlanEstudio plan;
    private long diasRestantes;
    private long totalTareas;
    private long tareasCompletadas;
    private int porcentaje;
    private boolean finalizado;
}