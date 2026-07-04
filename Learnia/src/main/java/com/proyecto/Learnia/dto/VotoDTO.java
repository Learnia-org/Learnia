package com.proyecto.Learnia.dto;

import com.proyecto.Learnia.entity.TipoVoto;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class VotoDTO {
    private TipoVoto tipoVoto;
    private Long idUsuario;
    private Long idRespuesta;
}
