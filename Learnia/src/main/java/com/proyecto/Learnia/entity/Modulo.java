package com.proyecto.Learnia.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "modulo")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Modulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_modulo")
    private Long idModulo;

    @NotNull(message = "El modulo debe tener un numero de orden")
    @Column(name = "numero")
    private Integer numero;

    @NotBlank(message = "El titulo no puede ir vacio")
    @Size(min = 2, max = 100, message = "El titulo debe tener entre 2 y 100 caracteres")
    @Column(name = "titulo")
    private String titulo;

    @Size(max = 255, message = "La descripcion debe tener maximo 255 caracteres")
    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "icono")
    private String icono;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    @NotNull(message = "El modulo debe pertenecer a una categoria")
    private Categoria categoria;

    @OneToMany(mappedBy = "modulo", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<ModuloPregunta> preguntas;
}
