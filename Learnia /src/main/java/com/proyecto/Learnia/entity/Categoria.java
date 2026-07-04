package com.proyecto.Learnia.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "categoria")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Long idCategoria;

    @NotBlank(message = "El nombre no puede ir vacio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Column(name = "nombre")
    private String nombreCategoria;

    @NotBlank(message = "La descripcion no puede ir vacia")
    @Size(min = 5, max = 200, message = "La descripcion debe tener entre 5 y 200 caracteres")
    @Column(name = "descripcion")
    private String descripcionCategoria;

    @Column(name = "activa", columnDefinition = "boolean default true")
    private boolean activa = true;

    @OneToMany(mappedBy = "categoria")
    private List<Pregunta> preguntas;
}