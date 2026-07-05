package com.proyecto.Learnia.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class MemoriaMensajeDTO {
    private String role;
    private String texto;
    private LocalDateTime fecha;
}
