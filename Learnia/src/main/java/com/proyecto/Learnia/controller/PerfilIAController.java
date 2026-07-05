package com.proyecto.Learnia.controller;

import com.proyecto.Learnia.dto.PanelIADTO;
import com.proyecto.Learnia.entity.Usuario;
import com.proyecto.Learnia.repository.CategoriaRepository;
import com.proyecto.Learnia.repository.UsuarioRepository;
import com.proyecto.Learnia.service.ErrorConceptualService;
import com.proyecto.Learnia.service.PerfilAprendizajeIAService;
import com.proyecto.Learnia.service.RutaAdaptativaService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Páginas del panel de Inteligencia Artificial del estudiante: perfil
 * inteligente, ruta adaptativa y práctica guiada con detección de errores.
 */
@Controller
@RequestMapping("/perfil-ia")
public class PerfilIAController {

    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final PerfilAprendizajeIAService perfilAprendizajeIAService;
    private final RutaAdaptativaService rutaAdaptativaService;
    private final ErrorConceptualService errorConceptualService;

    public PerfilIAController(UsuarioRepository usuarioRepository,
                               CategoriaRepository categoriaRepository,
                               PerfilAprendizajeIAService perfilAprendizajeIAService,
                               RutaAdaptativaService rutaAdaptativaService,
                               ErrorConceptualService errorConceptualService) {
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
        this.perfilAprendizajeIAService = perfilAprendizajeIAService;
        this.rutaAdaptativaService = rutaAdaptativaService;
        this.errorConceptualService = errorConceptualService;
    }

    private Usuario getUsuario(UserDetails userDetails) {
        return usuarioRepository.findByCorreoUsuario(userDetails.getUsername()).orElseThrow();
    }

    @GetMapping
    public String panel(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = getUsuario(userDetails);

        PanelIADTO panel = new PanelIADTO(
                perfilAprendizajeIAService.obtenerPerfilDTO(usuario),
                errorConceptualService.obtenerErroresRecientes(usuario),
                rutaAdaptativaService.construirRutaAdaptativa(usuario)
        );

        model.addAttribute("usuario", usuario);
        model.addAttribute("panel", panel);
        return "perfil-ia";
    }

    @GetMapping("/practicar")
    public String practicar(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = getUsuario(userDetails);
        model.addAttribute("usuario", usuario);
        model.addAttribute("categorias", categoriaRepository.findByActivaTrue());
        return "practicar-ia";
    }
}
