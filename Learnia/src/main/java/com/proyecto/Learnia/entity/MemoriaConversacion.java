package com.proyecto.Learnia.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Memoria persistente de la conversación del estudiante con el asistente de IA.
 * Permite que el chatbot recuerde el progreso y el contexto de conversaciones
 * anteriores aunque el estudiante cambie de dispositivo o borre el navegador.
 */
@Entity
@Table(name = "memoria_conversacion")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MemoriaConversacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_memoria")
    private Long idMemoria;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @NotBlank
    @Column(name = "rol", nullable = false, length = 10)
    private String rol;

    @NotBlank
    @Column(name = "contenido", columnDefinition = "TEXT", nullable = false)
    private String contenido;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();
}
