package com.proyecto.Learnia.service;

import com.proyecto.Learnia.dto.RecursoDTO;
import com.proyecto.Learnia.entity.Recurso;
import com.proyecto.Learnia.entity.TipoRecurso;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface RecursoService {

    List<Recurso> listar();
    Recurso crear(RecursoDTO recursoDTO);
    Recurso actualizarReC(Long id, RecursoDTO dto);

    Recurso subirArchivo(
            MultipartFile archivo,
            String titulo,
            String descripcion,
            TipoRecurso tipo,
            Long idUsuario,
            Long idCategoria
    ) throws IOException;

    Recurso buscarPorIdRec(Long id);
    void eliminar(Long id);

}
