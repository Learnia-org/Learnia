package com.proyecto.Learnia.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "modulo_pregunta")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ModuloPregunta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pregunta_modulo")
    private Long idPreguntaModulo;

    @NotBlank(message = "El enunciado no puede ir vacio")
    @Column(name = "enunciado", columnDefinition = "TEXT")
    private String enunciado;

    @NotBlank
    @Column(name = "opcion_a")
    private String opcionA;

    @NotBlank
    @Column(name = "opcion_b")
    private String opcionB;

    @NotBlank
    @Column(name = "opcion_c")
    private String opcionC;

    @NotBlank
    @Column(name = "opcion_d")
    private String opcionD;

    @NotBlank(message = "Debe indicarse la opcion correcta")
    @Column(name = "respuesta_correcta", length = 1)
    private String respuestaCorrecta;

    @Column(name = "explicacion", columnDefinition = "TEXT")
    private String explicacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_modulo", nullable = false)
    @NotNull(message = "La pregunta debe pertenecer a un modulo")
    @ToString.Exclude
    private Modulo modulo;
}
