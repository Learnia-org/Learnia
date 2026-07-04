package com.proyecto.Learnia.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pregunta")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Pregunta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pregunta")
    private Long idPregunta;

    @Column(nullable = false, length = 200)
    @Size(min = 15, max = 200, message = "El título debe tener entre 15 y 200 caracteres")
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Size(min = 20, max = 200, message = "La descripción debe tener entre 20 y 200 caracteres")
    private String descripcion;

    @Column(name = "fecha_publicacion")
    private LocalDateTime fechaPublicacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    @NotNull(message = "La pregunta debe tener un autor")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    @NotNull(message = "La pregunta debe pertenecer a una categoría")
    private Categoria categoria;

    @Column(name = "oculta", columnDefinition = "boolean default false")
    private boolean oculta = false;

    @OneToMany(mappedBy = "pregunta", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Respuesta> respuestas;
}