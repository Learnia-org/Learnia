package com.proyecto.Learnia.service;

import com.proyecto.Learnia.dto.RutaAdaptativaDTO;
import com.proyecto.Learnia.entity.Usuario;

public interface RutaAdaptativaService {

    /**
     * Construye una ruta de aprendizaje adaptativa: reordena y prioriza las
     * categorías/temas del estudiante según su rendimiento real (progreso,
     * errores conceptuales sin resolver y resultados de ejercicios).
     */
    RutaAdaptativaDTO construirRutaAdaptativa(Usuario usuario);
}
