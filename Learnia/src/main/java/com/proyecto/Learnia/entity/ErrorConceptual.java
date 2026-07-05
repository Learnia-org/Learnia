package com.proyecto.Learnia.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Registro de un error conceptual detectado por la IA, ya sea durante
 * una conversación con el chatbot o al evaluar un ejercicio del estudiante.
 * Sirve como memoria de errores para explicar al alumno por qué se equivocó
 * y para alimentar su perfil y su ruta de aprendizaje adaptativa.
 */
@Entity
@Table(name = "error_conceptual")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ErrorConceptual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_error")
    private Long idError;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;

    @Column(name = "tema", length = 200)
    private String tema;

    @Enumerated(EnumType.STRING)
    @Column(name = "origen", nullable = false)
    private OrigenError origen;

    @NotBlank
    @Column(name = "descripcion_error", columnDefinition = "TEXT")
    private String descripcionError;

    @NotBlank
    @Column(name = "explicacion_ia", columnDefinition = "TEXT")
    private String explicacionIA;

    @Column(name = "resuelto", columnDefinition = "boolean default false")
    private boolean resuelto = false;

    @Column(name = "fecha_deteccion", nullable = false)
    private LocalDateTime fechaDeteccion = LocalDateTime.now();

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;
}
