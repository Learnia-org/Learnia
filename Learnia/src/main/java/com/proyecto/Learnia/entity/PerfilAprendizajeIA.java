package com.proyecto.Learnia.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Perfil inteligente del estudiante generado y mantenido por la IA de Learnia.
 * Guarda fortalezas, debilidades, estilo y ritmo de aprendizaje detectados,
 * y las estadísticas base usadas para recalcular ese perfil con el tiempo.
 */
@Entity
@Table(name = "perfil_aprendizaje_ia")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PerfilAprendizajeIA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_perfil")
    private Long idPerfil;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false, unique = true)
    private Usuario usuario;

    @Column(name = "fortalezas", columnDefinition = "TEXT")
    private String fortalezas;

    @Column(name = "debilidades", columnDefinition = "TEXT")
    private String debilidades;

    @Enumerated(EnumType.STRING)
    @Column(name = "estilo_aprendizaje")
    private EstiloAprendizaje estiloAprendizaje;

    @Enumerated(EnumType.STRING)
    @Column(name = "ritmo_aprendizaje")
    private RitmoAprendizaje ritmoAprendizaje;

    @Column(name = "resumen_ia", columnDefinition = "TEXT")
    private String resumenIA;

    @Column(name = "nivel_confianza", columnDefinition = "int default 0")
    private int nivelConfianza = 0;

    @Column(name = "total_errores_detectados", columnDefinition = "int default 0")
    private int totalErroresDetectados = 0;

    @Column(name = "total_ejercicios_intentados", columnDefinition = "int default 0")
    private int totalEjerciciosIntentados = 0;

    @Column(name = "total_ejercicios_correctos", columnDefinition = "int default 0")
    private int totalEjerciciosCorrectos = 0;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    public PerfilAprendizajeIA(Usuario usuario) {
        this.usuario = usuario;
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }
}
