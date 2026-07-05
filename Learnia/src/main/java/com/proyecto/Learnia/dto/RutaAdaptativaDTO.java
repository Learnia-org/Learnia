package com.proyecto.Learnia.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class RutaAdaptativaDTO {
    private List<RecomendacionRutaDTO> recomendaciones;
    private PerfilIADTO perfil;
}
