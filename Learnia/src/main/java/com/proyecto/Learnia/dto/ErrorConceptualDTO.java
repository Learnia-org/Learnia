package com.proyecto.Learnia.dto;

import com.proyecto.Learnia.entity.OrigenError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ErrorConceptualDTO {
    private Long idError;
    private String categoriaNombre;
    private String tema;
    private OrigenError origen;
    private String descripcionError;
    private String explicacionIA;
    private boolean resuelto;
    private LocalDateTime fechaDeteccion;
}
