package com.proyecto.Learnia.service;

import com.proyecto.Learnia.dto.ModuloResultadoDTO;
import com.proyecto.Learnia.entity.Modulo;
import com.proyecto.Learnia.entity.ModuloPregunta;

import java.util.List;
import java.util.Map;

public interface ModuloService {

    List<Modulo> listarPorCategoria(Long idCategoria);

    Modulo buscarPorId(Long idModulo);

    Modulo buscarPorCategoriaYNumero(Long idCategoria, Integer numero);

    List<ModuloPregunta> listarPreguntas(Long idModulo);

    ModuloResultadoDTO corregir(Long idModulo, Map<Long, String> respuestas);
}
