package com.proyecto.Learnia.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class MensajeDTO {
    private Long idMensaje;
    private String contenido;
    private LocalDateTime fechaEnvio;
    private boolean propio;
    private Long idEmisor;
    private String nombreEmisor;
    private String fotoEmisor;
}
