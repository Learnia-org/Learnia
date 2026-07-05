package com.proyecto.Learnia.dto;

import com.proyecto.Learnia.entity.EstiloAprendizaje;
import com.proyecto.Learnia.entity.RitmoAprendizaje;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class PerfilIADTO {
    private Long idUsuario;
    private String nombreUsuario;
    private String fortalezas;
    private String debilidades;
    private EstiloAprendizaje estiloAprendizaje;
    private RitmoAprendizaje ritmoAprendizaje;
    private String resumenIA;
    private int nivelConfianza;
    private int totalErroresDetectados;
    private int totalEjerciciosIntentados;
    private int totalEjerciciosCorrectos;
    private LocalDateTime fechaActualizacion;
}
