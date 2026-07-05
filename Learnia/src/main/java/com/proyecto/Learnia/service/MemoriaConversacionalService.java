package com.proyecto.Learnia.service;

import com.proyecto.Learnia.dto.MemoriaMensajeDTO;
import com.proyecto.Learnia.entity.Usuario;

import java.util.List;

public interface MemoriaConversacionalService {

    void guardarMensaje(Usuario usuario, String rol, String contenido);

    List<MemoriaMensajeDTO> obtenerHistorialReciente(Usuario usuario, int limite);

    void limpiarHistorial(Usuario usuario);
}
