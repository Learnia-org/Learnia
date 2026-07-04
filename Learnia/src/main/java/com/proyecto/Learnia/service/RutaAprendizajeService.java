package com.proyecto.Learnia.service;

import com.proyecto.Learnia.dto.RutaAprendizajeDTO;
import com.proyecto.Learnia.dto.RutaResumenDTO;

import java.util.List;

public interface RutaAprendizajeService {

    RutaAprendizajeDTO obtenerRuta(Long idUsuario, Long idCategoria);

    List<RutaResumenDTO> obtenerResumenGeneral(Long idUsuario);

    void marcarProgreso(Long idUsuario, Long idRecurso, boolean completado, int nivelDominio);
}