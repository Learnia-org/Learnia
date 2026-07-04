package com.proyecto.Learnia.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "recurso_progreso", uniqueConstraints = {
        @UniqueConstraint(name = "uq_usuario_recurso_progreso", columnNames = {"id_usuario", "id_recurso"})
})
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RecursoProgreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_progreso")
    private Long idProgreso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_recurso", nullable = false)
    private Recurso recurso;

    @Column(name = "completado", columnDefinition = "boolean default false")
    private boolean completado = false;

    @Column(name = "nivel_dominio", columnDefinition = "int default 0")
    private int nivelDominio = 0;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    public boolean isDominado() {
        return completado && nivelDominio >= 4;
    }
}