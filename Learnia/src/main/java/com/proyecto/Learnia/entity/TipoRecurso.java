package com.proyecto.Learnia.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TipoRecurso {

    DOCUMENTO,
    PRESENTACION,
    AUDIO;

    @JsonCreator
    public static TipoRecurso from(String value) {
        if (value == null) return null;
        String val = value.trim().toUpperCase();
        for (TipoRecurso t : TipoRecurso.values()) {
            if (t.name().equals(val)) return t;
        }
        throw new IllegalArgumentException("El tipo de recurso '" + value + "' no es válido. Opciones: DOCUMENTO, PRESENTACION, AUDIO");
    }
}
