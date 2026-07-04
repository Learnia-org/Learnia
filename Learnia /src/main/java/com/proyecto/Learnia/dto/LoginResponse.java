package com.proyecto.Learnia.dto;

public class LoginResponse {
    public String message;
    public Long idUsuario;
    public String nombreUsuario;
    public String correoUsuario;

    public LoginResponse(String message, Long idUsuario, String nombreUsuario, String correoUsuario) {
        this.message = message;
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.correoUsuario = correoUsuario;
    }
}