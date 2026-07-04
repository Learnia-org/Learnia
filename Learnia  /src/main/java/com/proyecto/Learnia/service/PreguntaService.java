package com.proyecto.Learnia.service;

import com.proyecto.Learnia.dto.PreguntaDTO;
import com.proyecto.Learnia.entity.Pregunta;
import java.util.List;

public interface PreguntaService {
    List<Pregunta> listar();
    Pregunta buscarPorId(Long id);
    Pregunta guardar(PreguntaDTO dto);
    Pregunta actualizar(Long id, PreguntaDTO dto);
    void eliminar(Long id);
    List<Pregunta> buscarPorCategoria(Long idCategoria);
}