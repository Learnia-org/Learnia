package com.proyecto.Learnia.controller;

import com.proyecto.Learnia.entity.Pregunta;
import com.proyecto.Learnia.entity.Respuesta;
import com.proyecto.Learnia.entity.Usuario;
import com.proyecto.Learnia.repository.RespuestaRepository;
import com.proyecto.Learnia.repository.UsuarioRepository;
import com.proyecto.Learnia.service.PreguntaService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.proyecto.Learnia.entity.TipoVoto;
import com.proyecto.Learnia.repository.VotoRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Controller
public class PreguntaDetalleController {

    private final PreguntaService preguntaService;
    private final RespuestaRepository respuestaRepository;
    private final UsuarioRepository usuarioRepository;
    private final VotoRepository votoRepository;

    public PreguntaDetalleController(PreguntaService preguntaService,
                                     RespuestaRepository respuestaRepository,
                                     UsuarioRepository usuarioRepository, VotoRepository votoRepository) {
        this.preguntaService = preguntaService;
        this.respuestaRepository = respuestaRepository;
        this.usuarioRepository = usuarioRepository;
        this.votoRepository = votoRepository;
    }

    @GetMapping("/pregunta/{id}")
    public String verPregunta(@PathVariable Long id,
                              Model model,
                              @AuthenticationPrincipal UserDetails userDetails) {

        Pregunta pregunta = preguntaService.buscarPorId(id);

        Usuario usuario = usuarioRepository
                .findByCorreoUsuario(userDetails.getUsername())
                .orElseThrow();

        boolean esAutor = pregunta.getUsuario().getIdUsuario().equals(usuario.getIdUsuario());
        boolean esStaff = usuario.getRolUsuario() == com.proyecto.Learnia.entity.RolUsuario.ADMIN
                || usuario.getRolUsuario() == com.proyecto.Learnia.entity.RolUsuario.MODERADOR;

        if (pregunta.isOculta() && !esAutor && !esStaff) {
            return "redirect:/menu";
        }

        List<Respuesta> todas = respuestaRepository.findByPregunta_IdPregunta(id);
        List<Respuesta> respuestas = todas.stream()
                .filter(r -> !r.isOculta() || r.getUsuario().getIdUsuario().equals(usuario.getIdUsuario()))
                .toList();

        Map<Long, long[]> votosMap = new HashMap<>();
        Map<Long, String> votoUsuarioMap = new HashMap<>();

        for (Respuesta r : respuestas) {
            long likes    = votoRepository.countByRespuesta_IdRespuestaAndTipoVoto(r.getIdRespuesta(), TipoVoto.LIKE);
            long dislikes = votoRepository.countByRespuesta_IdRespuestaAndTipoVoto(r.getIdRespuesta(), TipoVoto.DISLIKE);
            votosMap.put(r.getIdRespuesta(), new long[]{likes, dislikes});

            List<com.proyecto.Learnia.entity.Voto> votosUsuario = votoRepository
                    .findAllByUsuario_IdUsuarioAndRespuesta_IdRespuestaOrderByIdVotoDesc(
                            usuario.getIdUsuario(), r.getIdRespuesta());
            if (!votosUsuario.isEmpty()) {
                votoUsuarioMap.put(r.getIdRespuesta(), votosUsuario.get(0).getTipoVoto().name());
            }
        }

        model.addAttribute("pregunta", pregunta);
        model.addAttribute("respuestas", respuestas);
        model.addAttribute("usuario", usuario);
        model.addAttribute("votosMap", votosMap);
        model.addAttribute("votoUsuarioMap", votoUsuarioMap);

        return "pregunta-detalle";
    }
}