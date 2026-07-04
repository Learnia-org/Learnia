package com.proyecto.Learnia.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TipoVoto {

    LIKE,
    DISLIKE;

    @JsonCreator
    public static TipoVoto from(String value) {
        if (value == null) return null;
        String val = value.trim().toUpperCase();
        for (TipoVoto v : TipoVoto.values()) {
            if (v.name().equals(val)) return v;
        }
        throw new IllegalArgumentException("El tipo de voto '" + value + "' no es válido. Opciones: LIKE, DISLIKE");
    }
}
