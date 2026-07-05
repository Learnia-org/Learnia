package com.proyecto.Learnia.service;

import com.proyecto.Learnia.dto.EvaluarRespuestaRequestDTO;
import com.proyecto.Learnia.dto.EvaluarRespuestaResponseDTO;
import com.proyecto.Learnia.entity.Usuario;

public interface AnalisisConceptualService {

    /**
     * Evalúa la respuesta de un estudiante a un ejercicio o duda puntual,
     * detecta si hay un error conceptual, guarda el intento y, de haber error,
     * lo registra en la memoria de errores del estudiante y actualiza su perfil.
     */
    EvaluarRespuestaResponseDTO evaluarRespuesta(Usuario usuario, EvaluarRespuestaRequestDTO request);
}
