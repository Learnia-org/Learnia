package com.proyecto.Learnia.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Intento de un estudiante al responder un ejercicio o pregunta de práctica
 * evaluado por la IA. Es la base para detectar errores conceptuales y medir
 * el rendimiento real del estudiante por tema y categoría.
 */
@Entity
@Table(name = "intento_ejercicio")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class IntentoEjercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_intento")
    private Long idIntento;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_recurso")
    private Recurso recurso;

    @NotBlank
    @Column(name = "enunciado", columnDefinition = "TEXT", nullable = false)
    private String enunciado;

    @NotBlank
    @Column(name = "respuesta_estudiante", columnDefinition = "TEXT", nullable = false)
    private String respuestaEstudiante;

    @Column(name = "es_correcta", nullable = false)
    private boolean esCorrecta;

    @Column(name = "tipo_error_conceptual", length = 150)
    private String tipoErrorConceptual;

    @Column(name = "explicacion_ia", columnDefinition = "TEXT")
    private String explicacionIA;

    @Column(name = "sugerencia_ia", columnDefinition = "TEXT")
    private String sugerenciaIA;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();
}
