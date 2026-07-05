package com.proyecto.Learnia.controller;

import com.proyecto.Learnia.dto.ConversacionDTO;
import com.proyecto.Learnia.dto.MensajeDTO;
import com.proyecto.Learnia.entity.Usuario;
import com.proyecto.Learnia.repository.UsuarioRepository;
import com.proyecto.Learnia.service.MensajeService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class MensajeController {

    private final MensajeService mensajeService;
    private final UsuarioRepository usuarioRepository;

    public MensajeController(MensajeService mensajeService, UsuarioRepository usuarioRepository) {
        this.mensajeService = mensajeService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/mensajes")
    public String verMensajes(@RequestParam(value = "con", required = false) Long con,
                               Model model,
                               @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByCorreoUsuario(userDetails.getUsername()).orElseThrow();

        List<ConversacionDTO> conversaciones = mensajeService.listarConversaciones(usuario.getIdUsuario());
        Usuario asistente = usuarioRepository.findByEsBotTrue().orElse(null);

        model.addAttribute("usuario", usuario);
        model.addAttribute("asistente", asistente);

        if (asistente != null) {
            ConversacionDTO conversacionAsistente = conversaciones.stream()
                    .filter(c -> c.getIdUsuario().equals(asistente.getIdUsuario()))
                    .findFirst()
                    .orElse(null);
            model.addAttribute("conversacionAsistente", conversacionAsistente);

            List<ConversacionDTO> resto = conversaciones.stream()
                    .filter(c -> !c.getIdUsuario().equals(asistente.getIdUsuario()))
                    .toList();
            model.addAttribute("conversaciones", resto);
        } else {
            model.addAttribute("conversaciones", conversaciones);
        }

        if (con != null) {
            usuarioRepository.findById(con).ifPresent(otro -> model.addAttribute("contactoInicial", otro));
        }

        return "mensajes";
    }

    @GetMapping("/api/mensajes/buscar-usuarios")
    @ResponseBody
    public List<Map<String, Object>> buscarUsuarios(@RequestParam("q") String q,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByCorreoUsuario(userDetails.getUsername()).orElseThrow();
        if (q == null || q.trim().length() < 2) {
            return List.of();
        }
        return usuarioRepository
                .findByNombreUsuarioContainingIgnoreCaseAndIdUsuarioNot(q.trim(), usuario.getIdUsuario())
                .stream()
                .filter(u -> !u.isEsBot())
                .map(u -> Map.<String, Object>of(
                        "idUsuario", u.getIdUsuario(),
                        "nombreUsuario", u.getNombreUsuario(),
                        "fotoUsuario", u.getFotoUsuario() != null ? u.getFotoUsuario() : "",
                        "enLinea", u.isEnLinea()
                ))
                .collect(Collectors.toList());
    }

    @GetMapping("/api/mensajes/conversaciones")
    @ResponseBody
    public List<ConversacionDTO> conversaciones(@AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByCorreoUsuario(userDetails.getUsername()).orElseThrow();
        return mensajeService.listarConversaciones(usuario.getIdUsuario());
    }

    @GetMapping("/api/mensajes/conversacion/{idOtroUsuario}")
    @ResponseBody
    public List<MensajeDTO> conversacion(@PathVariable Long idOtroUsuario,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByCorreoUsuario(userDetails.getUsername()).orElseThrow();
        return mensajeService.obtenerConversacion(usuario.getIdUsuario(), idOtroUsuario);
    }

    @PostMapping("/api/mensajes/enviar")
    @ResponseBody
    public MensajeDTO enviar(@RequestBody Map<String, String> body,
                              @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByCorreoUsuario(userDetails.getUsername()).orElseThrow();
        Long receptorId = Long.valueOf(body.get("receptorId"));
        String contenido = body.get("contenido");
        return mensajeService.enviarMensaje(usuario.getIdUsuario(), receptorId, contenido);
    }

    @GetMapping("/api/mensajes/no-leidos")
    @ResponseBody
    public Map<String, Long> noLeidos(@AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByCorreoUsuario(userDetails.getUsername()).orElseThrow();
        return Map.of("noLeidos", mensajeService.contarNoLeidos(usuario.getIdUsuario()));
    }
}
