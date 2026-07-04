package com.proyecto.Learnia.service;

import java.util.List;
import com.proyecto.Learnia.entity.Respuesta;

public interface RespuestaService {

    List<Respuesta> listar();
    Respuesta guardar(Long respuestaId, String contenido);
    Respuesta guardarComoIA(Long preguntaId, String contenido);
    Respuesta buscarPorId(Long id);
    Respuesta actualizar(Long id, Respuesta respuesta);
    void eliminar(Long id);
}