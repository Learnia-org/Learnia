package com.proyecto.Learnia.dto;

import com.proyecto.Learnia.entity.Categoria;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class RutaAprendizajeDTO {
    private Categoria categoria;
    private List<RecursoRutaDTO> recursos;
    private RecursoRutaDTO recomendado;
    private int totalRecursos;
    private int dominados;
    private int porcentaje;
}