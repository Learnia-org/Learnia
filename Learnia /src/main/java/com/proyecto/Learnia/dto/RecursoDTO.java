package com.proyecto.Learnia.dto;

import com.proyecto.Learnia.entity.TipoRecurso;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class RecursoDTO {
    private String tituloRecurso;
    private String descripcionRecurso;
    private TipoRecurso tipoRecurso;
    private String urlArchivo;
    private LocalDateTime fechaSubida;
    private Long idUsuario;
    private Long idCategoria;
}
