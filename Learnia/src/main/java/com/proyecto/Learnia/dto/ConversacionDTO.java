package com.proyecto.Learnia.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ConversacionDTO {
    private Long idUsuario;
    private String nombreUsuario;
    private String fotoUsuario;
    private boolean enLinea;
    private String ultimoMensaje;
    private LocalDateTime fechaUltimoMensaje;
    private long noLeidos;
}
