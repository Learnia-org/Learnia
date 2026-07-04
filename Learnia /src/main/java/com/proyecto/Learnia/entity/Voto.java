package com.proyecto.Learnia.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "voto")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Voto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_voto")
    private Long idVoto;

    @NotNull(message = "El tipo de voto es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_voto", nullable = false)
    private TipoVoto tipoVoto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    @NotNull(message = "El usuario es obligatorio")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_respuesta", nullable = false)
    @NotNull(message = "La respuesta es obligatoria")
    private Respuesta respuesta;

}

