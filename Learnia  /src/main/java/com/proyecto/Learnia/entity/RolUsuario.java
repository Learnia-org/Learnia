package com.proyecto.Learnia.entity;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum RolUsuario {
    ADMIN,
    MODERADOR,
    ESTUDIANTE;

    @JsonCreator
    public static RolUsuario from(String value) {
        if (value == null) return null;
        String valorBusqueda = value.trim().toUpperCase();
        for (RolUsuario rol : RolUsuario.values()) {
            if (rol.name().equals(valorBusqueda)) {
                return rol;
            }
        }
        throw new IllegalArgumentException("El rol '" + value + "' no existe. Los valores permitidos son: ADMIN, MODERADOR, ESTUDIANTE.");
    }
}