package com.proyecto.Learnia.service;

import com.proyecto.Learnia.dto.MemoriaMensajeDTO;
import com.proyecto.Learnia.entity.MemoriaConversacion;
import com.proyecto.Learnia.entity.Usuario;
import com.proyecto.Learnia.repository.MemoriaConversacionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MemoriaConversacionalServiceImpl implements MemoriaConversacionalService {

    private static final int LIMITE_ALMACENADO = 60;

    private final MemoriaConversacionRepository memoriaConversacionRepository;

    public MemoriaConversacionalServiceImpl(MemoriaConversacionRepository memoriaConversacionRepository) {
        this.memoriaConversacionRepository = memoriaConversacionRepository;
    }

    @Override
    public void guardarMensaje(Usuario usuario, String rol, String contenido) {
        if (contenido == null || contenido.isBlank()) {
            return;
        }
        MemoriaConversacion mensaje = new MemoriaConversacion();
        mensaje.setUsuario(usuario);
        mensaje.setRol(rol);
        mensaje.setContenido(contenido);
        memoriaConversacionRepository.save(mensaje);
    }

    @Override
    public List<MemoriaMensajeDTO> obtenerHistorialReciente(Usuario usuario, int limite) {
        int tope = Math.min(limite, LIMITE_ALMACENADO);
        List<com.proyecto.Learnia.entity.MemoriaConversacion> ultimos = memoriaConversacionRepository
                .findByUsuario_IdUsuarioOrderByFechaDesc(usuario.getIdUsuario(), PageRequest.of(0, tope));

        java.util.Collections.reverse(ultimos);

        return ultimos.stream()
                .map(m -> new MemoriaMensajeDTO(m.getRol(), m.getContenido(), m.getFecha()))
                .toList();
    }

    @Override
    @Transactional
    public void limpiarHistorial(Usuario usuario) {
        memoriaConversacionRepository.deleteByUsuario_IdUsuario(usuario.getIdUsuario());
    }
}
