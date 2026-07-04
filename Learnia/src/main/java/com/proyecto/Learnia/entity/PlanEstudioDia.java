package com.proyecto.Learnia.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "plan_estudio_dia")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PlanEstudioDia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dia")
    private Long idDia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_plan", nullable = false)
    @ToString.Exclude
    private PlanEstudio planEstudio;

    @Column(name = "numero_dia", nullable = false)
    private int numeroDia;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_recurso")
    private Recurso recurso;

    @Column(name = "titulo_tema", nullable = false, length = 200)
    private String tituloTema;

    @Column(name = "completado", columnDefinition = "boolean default false")
    private boolean completado = false;
}