package com.proyecto.Learnia.service;

import com.proyecto.Learnia.dto.PerfilIADTO;
import com.proyecto.Learnia.entity.PerfilAprendizajeIA;
import com.proyecto.Learnia.entity.Usuario;

public interface PerfilAprendizajeIAService {

    PerfilAprendizajeIA obtenerOCrearPerfil(Usuario usuario);

    PerfilIADTO obtenerPerfilDTO(Usuario usuario);

    void registrarResultadoEjercicio(Usuario usuario, boolean correcta);

    void actualizarPerfilConIA(Usuario usuario);
}
