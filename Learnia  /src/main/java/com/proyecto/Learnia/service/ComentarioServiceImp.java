package com.proyecto.Learnia.service;

import com.proyecto.Learnia.entity.Comentario;
import com.proyecto.Learnia.exception.SuccesException;
import com.proyecto.Learnia.repository.ComentarioRepository;
import com.proyecto.Learnia.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ComentarioServiceImp implements ComentarioService {

    private final ComentarioRepository comentarioRepository;

    public ComentarioServiceImp(ComentarioRepository comentarioRepository) {
        this.comentarioRepository = comentarioRepository;
    }

    @Override
    public List<Comentario> listar() {
        return comentarioRepository.findAll();
    }

    @Override
    public Comentario crear(Comentario comentario) {
        comentario.setIdComentario(null);
        comentario.setFecha(LocalDateTime.now());
        return comentarioRepository.save(comentario);
    }

    @Override
    public Comentario buscarPorId(Long id) {
        return comentarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comentario no encontrado con ID: " + id));
    }

    @Override
    public Comentario actualizar(Long id, Comentario comentario) {
        Comentario existente = buscarPorId(id);

        existente.setContenido(comentario.getContenido());
        existente.setFecha(comentario.getFecha());
        existente.setRecurso(comentario.getRecurso());
        existente.setRespuesta(comentario.getRespuesta());
        existente.setUsuario(comentario.getUsuario());
        return comentarioRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        if (!comentarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Comentario no encontrado con ID: " + id);
        }
        comentarioRepository.deleteById(id);
        throw new SuccesException("Comentario eliminado correctamente");
    }
}
