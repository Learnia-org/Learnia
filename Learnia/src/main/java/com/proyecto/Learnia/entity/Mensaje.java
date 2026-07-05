package com.proyecto.Learnia.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "mensaje")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mensaje")
    private Long idMensaje;

    @NotBlank(message = "El mensaje no puede estar vacío")
    @Size(min = 1, max = 1000, message = "El mensaje debe tener entre 1 y 1000 caracteres")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "fecha_envio", nullable = false)
    private LocalDateTime fechaEnvio = LocalDateTime.now();

    @Column(name = "leido", columnDefinition = "boolean default false")
    private boolean leido = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_emisor", nullable = false)
    @NotNull(message = "El emisor es obligatorio")
    private Usuario emisor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_receptor", nullable = false)
    @NotNull(message = "El receptor es obligatorio")
    private Usuario receptor;
}
