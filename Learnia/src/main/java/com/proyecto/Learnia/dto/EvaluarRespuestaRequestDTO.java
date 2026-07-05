package com.proyecto.Learnia.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EvaluarRespuestaRequestDTO {

    private Long idCategoria;

    private Long idRecurso;

    @NotBlank(message = "El enunciado o tema no puede estar vacio")
    private String enunciado;

    @NotBlank(message = "La respuesta no puede estar vacia")
    private String respuesta;
}
