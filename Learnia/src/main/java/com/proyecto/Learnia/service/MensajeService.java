package com.proyecto.Learnia.service;

import com.proyecto.Learnia.dto.ConversacionDTO;
import com.proyecto.Learnia.dto.MensajeDTO;

import java.util.List;

public interface MensajeService {
    MensajeDTO enviarMensaje(Long idEmisor, Long idReceptor, String contenido);
    List<MensajeDTO> obtenerConversacion(Long idUsuarioActual, Long idOtroUsuario);
    List<ConversacionDTO> listarConversaciones(Long idUsuarioActual);
    long contarNoLeidos(Long idUsuarioActual);
}
