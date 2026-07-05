package com.proyecto.Learnia.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RecomendacionRutaDTO {
    private Long idCategoria;
    private String nombreCategoria;
    private String prioridad;
    private String motivo;
    private int porcentaje;
    private Long idRecursoSugerido;
    private String tituloRecursoSugerido;
}
