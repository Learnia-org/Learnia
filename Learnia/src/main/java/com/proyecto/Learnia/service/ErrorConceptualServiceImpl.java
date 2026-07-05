package com.proyecto.Learnia.service;

import com.proyecto.Learnia.dto.ErrorConceptualDTO;
import com.proyecto.Learnia.entity.ErrorConceptual;
import com.proyecto.Learnia.entity.Usuario;
import com.proyecto.Learnia.exception.ResourceNotFoundException;
import com.proyecto.Learnia.repository.ErrorConceptualRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ErrorConceptualServiceImpl implements ErrorConceptualService {

    private final ErrorConceptualRepository errorConceptualRepository;

    public ErrorConceptualServiceImpl(ErrorConceptualRepository errorConceptualRepository) {
        this.errorConceptualRepository = errorConceptualRepository;
    }

    @Override
    public List<ErrorConceptualDTO> obtenerErroresRecientes(Usuario usuario) {
        return errorConceptualRepository
                .findTop10ByUsuario_IdUsuarioOrderByFechaDeteccionDesc(usuario.getIdUsuario())
                .stream()
                .map(this::convertir)
                .toList();
    }

    @Override
    public List<ErrorConceptualDTO> obtenerErroresPendientes(Usuario usuario) {
        return errorConceptualRepository
                .findByUsuario_IdUsuarioAndResueltoFalseOrderByFechaDeteccionDesc(usuario.getIdUsuario())
                .stream()
                .map(this::convertir)
                .toList();
    }

    @Override
    public void marcarResuelto(Usuario usuario, Long idError) {
        ErrorConceptual error = errorConceptualRepository.findById(idError)
                .orElseThrow(() -> new ResourceNotFoundException("Error conceptual no encontrado"));

        if (!error.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new ResourceNotFoundException("Error conceptual no encontrado");
        }

        error.setResuelto(true);
        error.setFechaResolucion(LocalDateTime.now());
        errorConceptualRepository.save(error);
    }

    private ErrorConceptualDTO convertir(ErrorConceptual error) {
        return new ErrorConceptualDTO(
                error.getIdError(),
                error.getCategoria() != null ? error.getCategoria().getNombreCategoria() : "General",
                error.getTema(),
                error.getOrigen(),
                error.getDescripcionError(),
                error.getExplicacionIA(),
                error.isResuelto(),
                error.getFechaDeteccion()
        );
    }
}
