package com.proyecto.Learnia.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table(name = "recurso")
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Recurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recurso")
    private Long idRecurso;

    @NotBlank(message = "El titulo no puede estar vacio")
    @Size(min = 2, max = 100, message = "El titulo debe tener entre 2 y 60 caracteres")
    @Column(name = "titulo_recurso")
    private String tituloRecurso;

    @Column(name = "descripcion_recurso")
    @Size(min = 5, max = 200, message = "La descripcion debe estar entre 5 y 200 caracteres")
    private String descripcionRecurso;

    @NotNull(message = "Debe de seleccionar el tipo de recurso")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_recurso")
    private TipoRecurso tipoRecurso;

    @NotBlank(message = "Debe de seleccionar el archivo")
    @Column(name = "url_archivo")
    private String urlArchivo;

    @Column(name = "fecha_subida")
    private LocalDateTime fechaSubida;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    @NotNull
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    @NotNull
    private Categoria categoria;
}
