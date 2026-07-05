package com.proyecto.Learnia.controller;

import com.proyecto.Learnia.dto.MentorDTO;
import com.proyecto.Learnia.entity.Usuario;
import com.proyecto.Learnia.repository.RespuestaRepository;
import com.proyecto.Learnia.repository.UsuarioRepository;
import com.proyecto.Learnia.repository.VotoRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class RankingController {

    private final RespuestaRepository respuestaRepository;
    private final VotoRepository votoRepository;
    private final UsuarioRepository usuarioRepository;

    public RankingController(RespuestaRepository respuestaRepository,
                             VotoRepository votoRepository,
                             UsuarioRepository usuarioRepository) {
        this.respuestaRepository = respuestaRepository;
        this.votoRepository = votoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/ranking-mentores")
    public String ranking(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository
                .findByCorreoUsuario(userDetails.getUsername())
                .orElseThrow();

        Map<Long, Long> respuestasPorUsuario = new HashMap<>();
        for (Object[] fila : respuestaRepository.countRespuestasPorUsuario()) {
            respuestasPorUsuario.put((Long) fila[0], (Long) fila[1]);
        }

        Map<Long, Long> likesPorUsuario = new HashMap<>();
        for (Object[] fila : votoRepository.countLikesPorUsuario()) {
            likesPorUsuario.put((Long) fila[0], (Long) fila[1]);
        }

        List<MentorDTO> mentores = new ArrayList<>();
        for (Map.Entry<Long, Long> entry : respuestasPorUsuario.entrySet()) {
            Long idUsuario = entry.getKey();
            usuarioRepository.findById(idUsuario).ifPresent(u -> {
                if (u.isEsBot()) return;
                long totalRespuestas = entry.getValue();
                long totalLikes = likesPorUsuario.getOrDefault(idUsuario, 0L);
                long puntaje = (totalRespuestas * 10) + (totalLikes * 5);
                mentores.add(new MentorDTO(u, totalRespuestas, totalLikes, puntaje));
            });
        }

        mentores.sort((a, b) -> Long.compare(b.puntaje(), a.puntaje()));
        List<MentorDTO> top = mentores.size() > 20 ? mentores.subList(0, 20) : mentores;

        model.addAttribute("usuario", usuario);
        model.addAttribute("mentores", top);

        return "ranking-mentores";
    }
}
