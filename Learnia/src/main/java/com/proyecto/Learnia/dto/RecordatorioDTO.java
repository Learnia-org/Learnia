package com.proyecto.Learnia.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RecordatorioDTO {
    private String mensaje;
    private String tipo; // "hoy", "atrasado" o "examen"
    private Long idPlan;
}