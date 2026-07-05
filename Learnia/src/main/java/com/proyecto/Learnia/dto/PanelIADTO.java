package com.proyecto.Learnia.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PanelIADTO {
    private PerfilIADTO perfil;
    private List<ErrorConceptualDTO> erroresRecientes;
    private RutaAdaptativaDTO rutaAdaptativa;
}
