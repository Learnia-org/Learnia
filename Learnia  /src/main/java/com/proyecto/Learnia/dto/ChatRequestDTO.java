package com.proyecto.Learnia.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChatRequestDTO {

    @NotBlank(message = "El mensaje no puede estar vacio")
    private String mensaje;

    private List<ChatMensajeDTO> historial;
}
