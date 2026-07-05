package com.proyecto.Learnia.service;

import com.proyecto.Learnia.dto.ModuloResultadoDTO;
import com.proyecto.Learnia.entity.Modulo;
import com.proyecto.Learnia.entity.ModuloPregunta;
import com.proyecto.Learnia.exception.ResourceNotFoundException;
import com.proyecto.Learnia.repository.ModuloPreguntaRepository;
import com.proyecto.Learnia.repository.ModuloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ModuloServiceImpl implements ModuloService {

    @Autowired
    private ModuloRepository moduloRepository;

    @Autowired
    private ModuloPreguntaRepository moduloPreguntaRepository;

    @Override
    public List<Modulo> listarPorCategoria(Long idCategoria) {
        return moduloRepository.findByCategoria_IdCategoriaOrderByNumeroAsc(idCategoria);
    }

    @Override
    public Modulo buscarPorId(Long idModulo) {
        return moduloRepository.findById(idModulo)
                .orElseThrow(() -> new ResourceNotFoundException("El modulo con id: " + idModulo + " no fue encontrado"));
    }

    @Override
    public Modulo buscarPorCategoriaYNumero(Long idCategoria, Integer numero) {
        return moduloRepository.findByCategoria_IdCategoriaAndNumero(idCategoria, numero)
                .orElseThrow(() -> new ResourceNotFoundException("El modulo numero " + numero + " no fue encontrado para esta materia"));
    }

    @Override
    public List<ModuloPregunta> listarPreguntas(Long idModulo) {
        return moduloPreguntaRepository.findByModulo_IdModuloOrderByIdPreguntaModuloAsc(idModulo);
    }

    @Override
    public ModuloResultadoDTO corregir(Long idModulo, Map<Long, String> respuestas) {
        List<ModuloPregunta> preguntas = listarPreguntas(idModulo);

        ModuloResultadoDTO resultado = new ModuloResultadoDTO();
        resultado.setTotalPreguntas(preguntas.size());

        int correctas = 0;
        for (ModuloPregunta pregunta : preguntas) {
            String respuestaUsuario = respuestas.get(pregunta.getIdPreguntaModulo());
            boolean esCorrecta = respuestaUsuario != null
                    && respuestaUsuario.equalsIgnoreCase(pregunta.getRespuestaCorrecta());

            if (respuestaUsuario != null) {
                resultado.getRespuestasUsuario().put(pregunta.getIdPreguntaModulo(), respuestaUsuario);
            }
            resultado.getAciertos().put(pregunta.getIdPreguntaModulo(), esCorrecta);

            if (esCorrecta) {
                correctas++;
            }
        }

        resultado.setCorrectas(correctas);
        return resultado;
    }
}
