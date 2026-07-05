package com.proyecto.Learnia.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EvaluarRespuestaResponseDTO {
    private boolean correcta;
    private String tipoError;
    private String explicacion;
    private String sugerencia;
    private PerfilIADTO perfil;
}
