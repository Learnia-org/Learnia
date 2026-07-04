package com.proyecto.Learnia.controller;

import com.proyecto.Learnia.dto.RutaAprendizajeDTO;
import com.proyecto.Learnia.entity.Usuario;
import com.proyecto.Learnia.repository.UsuarioRepository;
import com.proyecto.Learnia.service.RutaAprendizajeService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/ruta-aprendizaje")
public class RutaAprendizajeController {

    private final RutaAprendizajeService rutaAprendizajeService;
    private final UsuarioRepository usuarioRepository;

    public RutaAprendizajeController(RutaAprendizajeService rutaAprendizajeService,
                                     UsuarioRepository usuarioRepository) {
        this.rutaAprendizajeService = rutaAprendizajeService;
        this.usuarioRepository = usuarioRepository;
    }

    private Usuario getUsuario(UserDetails userDetails) {
        return usuarioRepository.findByCorreoUsuario(userDetails.getUsername()).orElseThrow();
    }

    @GetMapping
    public String panel(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = getUsuario(userDetails);
        model.addAttribute("usuario", usuario);
        model.addAttribute("resumen", rutaAprendizajeService.obtenerResumenGeneral(usuario.getIdUsuario()));
        return "ruta-aprendizaje";
    }

    @GetMapping("/{idCategoria}")
    public String detalle(@PathVariable Long idCategoria, Model model,
                          @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = getUsuario(userDetails);
        RutaAprendizajeDTO ruta = rutaAprendizajeService.obtenerRuta(usuario.getIdUsuario(), idCategoria);

        model.addAttribute("usuario", usuario);
        model.addAttribute("ruta", ruta);
        return "ruta-aprendizaje-detalle";
    }

    @PostMapping("/progreso")
    public String marcarProgreso(@RequestParam Long idRecurso,
                                 @RequestParam Long idCategoria,
                                 @RequestParam(required = false) Boolean completado,
                                 @RequestParam(defaultValue = "0") int nivelDominio,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = getUsuario(userDetails);
        boolean marcadoCompletado = completado != null && completado;
        rutaAprendizajeService.marcarProgreso(usuario.getIdUsuario(), idRecurso, marcadoCompletado, nivelDominio);
        return "redirect:/ruta-aprendizaje/" + idCategoria;
    }
}