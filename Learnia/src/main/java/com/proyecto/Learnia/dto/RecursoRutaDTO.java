package com.proyecto.Learnia.dto;

import com.proyecto.Learnia.entity.Recurso;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RecursoRutaDTO {
    private Recurso recurso;
    private boolean completado;
    private int nivelDominio;
    private boolean dominado;
}