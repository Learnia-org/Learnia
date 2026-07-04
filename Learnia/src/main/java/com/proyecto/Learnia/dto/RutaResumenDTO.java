package com.proyecto.Learnia.dto;

import com.proyecto.Learnia.entity.Categoria;
import com.proyecto.Learnia.entity.Recurso;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RutaResumenDTO {
    private Categoria categoria;
    private int totalRecursos;
    private int dominados;
    private int porcentaje;
    private Recurso siguienteRecomendado;
}