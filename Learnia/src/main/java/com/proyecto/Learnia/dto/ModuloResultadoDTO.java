package com.proyecto.Learnia.dto;

import java.util.HashMap;
import java.util.Map;

public class ModuloResultadoDTO {

    private int totalPreguntas;
    private int correctas;
    private final Map<Long, String> respuestasUsuario = new HashMap<>();
    private final Map<Long, Boolean> aciertos = new HashMap<>();

    public int getTotalPreguntas() {
        return totalPreguntas;
    }

    public void setTotalPreguntas(int totalPreguntas) {
        this.totalPreguntas = totalPreguntas;
    }

    public int getCorrectas() {
        return correctas;
    }

    public void setCorrectas(int correctas) {
        this.correctas = correctas;
    }

    public Map<Long, String> getRespuestasUsuario() {
        return respuestasUsuario;
    }

    public Map<Long, Boolean> getAciertos() {
        return aciertos;
    }

    public int getPorcentaje() {
        if (totalPreguntas == 0) return 0;
        return (int) Math.round((correctas * 100.0) / totalPreguntas);
    }

    public boolean isAprobado() {
        return getPorcentaje() >= 60;
    }
}
