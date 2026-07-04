package com.proyecto.Learnia.service;

import com.proyecto.Learnia.dto.VotoDTO;
import com.proyecto.Learnia.entity.Voto;

import java.util.List;

public interface VotoService {
    List<Voto> listar();
    Voto crear(VotoDTO votoDTO);
    Voto buscarPorId(Long id);
    Voto actualizar(Long id, VotoDTO dto);
    void eliminar(Long id);
}