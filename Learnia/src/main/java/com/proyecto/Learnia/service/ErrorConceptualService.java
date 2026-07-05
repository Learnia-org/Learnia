package com.proyecto.Learnia.service;

import com.proyecto.Learnia.dto.ErrorConceptualDTO;
import com.proyecto.Learnia.entity.Usuario;

import java.util.List;

public interface ErrorConceptualService {

    List<ErrorConceptualDTO> obtenerErroresRecientes(Usuario usuario);

    List<ErrorConceptualDTO> obtenerErroresPendientes(Usuario usuario);

    void marcarResuelto(Usuario usuario, Long idError);
}
