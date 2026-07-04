package com.proyecto.Learnia.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RespuestaDTO {
    private String contenido;
    private LocalDateTime fechaRespuesta;
    private Long idUsuario;
    private Long idPregunta;
}
