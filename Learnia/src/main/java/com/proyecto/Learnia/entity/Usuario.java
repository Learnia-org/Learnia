package com.proyecto.Learnia.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuario", uniqueConstraints = {
        @UniqueConstraint(columnNames = "correo_usuario")
})
@Getter
@Setter
@ToString
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @NotBlank(message = "El nombre del usuario no puede estar vacio")
    @Size(min = 2, max = 100, message = "Numero de caracteres invalido en el nombre")
    @Column(name = "nombre_usuario")
    private String nombreUsuario;

    @NotBlank(message = "El correo no puede estar vacio")
    @Email(message = "Formato de correo invalido")
    @Size(min = 5, max = 100, message = "Numero de caracteres invalido en el correo")
    @Column(name = "correo_usuario", unique = true)
    private String correoUsuario;

    @NotBlank(message = "La contraseña no puede estar vacia")
    @Size(min = 8, max = 100, message = "Numero de caracteres invalido en la contraseña")
    @Column(name = "contrasena")
    private String contrasenaUsuario;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol")
    private RolUsuario rolUsuario;

    @Column(name = "foto")
    private String fotoUsuario;

    @Column(name = "en_linea", columnDefinition = "boolean default false")
    private boolean enLinea = false;

    @Column(name = "bloqueado", columnDefinition = "boolean default false")
    private boolean bloqueado = false;

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    public Usuario(String nombreUsuario, String correoUsuario, String contrasenaUsuario) {
        this.nombreUsuario = nombreUsuario;
        this.correoUsuario = correoUsuario;
        this.contrasenaUsuario = contrasenaUsuario;
        this.fechaRegistro = LocalDateTime.now();
    }

    public Usuario() {
    }
}