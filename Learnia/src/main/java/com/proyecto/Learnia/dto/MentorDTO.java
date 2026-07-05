package com.proyecto.Learnia.dto;

import com.proyecto.Learnia.entity.Usuario;

public record MentorDTO(Usuario usuario, long totalRespuestas, long totalLikes, long puntaje) {
}
