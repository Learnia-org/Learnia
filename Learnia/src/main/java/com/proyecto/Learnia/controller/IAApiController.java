package com.proyecto.Learnia.controller;

import com.proyecto.Learnia.dto.ErrorConceptualDTO;
import com.proyecto.Learnia.dto.EvaluarRespuestaRequestDTO;
import com.proyecto.Learnia.dto.EvaluarRespuestaResponseDTO;
import com.proyecto.Learnia.dto.PerfilIADTO;
import com.proyecto.Learnia.dto.RutaAdaptativaDTO;
import com.proyecto.Learnia.entity.Usuario;
import com.proyecto.Learnia.repository.UsuarioRepository;
import com.proyecto.Learnia.service.AnalisisConceptualService;
import com.proyecto.Learnia.service.ErrorConceptualService;
import com.proyecto.Learnia.service.PerfilAprendizajeIAService;
import com.proyecto.Learnia.service.RutaAdaptativaService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API REST de las funcionalidades de Inteligencia Artificial y aprendizaje
 * personalizado: detección de errores conceptuales, perfil inteligente del
 * estudiante y ruta de aprendizaje adaptativa.
 */
@RestController
@RequestMapping("/api/ia")
public class IAApiController {

    private final UsuarioRepository usuarioRepository;
    private final AnalisisConceptualService analisisConceptualService;
    private final PerfilAprendizajeIAService perfilAprendizajeIAService;
    private final RutaAdaptativaService rutaAdaptativaService;
    private final ErrorConceptualService errorConceptualService;

    public IAApiController(UsuarioRepository usuarioRepository,
                            AnalisisConceptualService analisisConceptualService,
                            PerfilAprendizajeIAService perfilAprendizajeIAService,
                            RutaAdaptativaService rutaAdaptativaService,
                            ErrorConceptualService errorConceptualService) {
        this.usuarioRepository = usuarioRepository;
        this.analisisConceptualService = analisisConceptualService;
        this.perfilAprendizajeIAService = perfilAprendizajeIAService;
        this.rutaAdaptativaService = rutaAdaptativaService;
        this.errorConceptualService = errorConceptualService;
    }

    private Usuario getUsuario(UserDetails userDetails) {
        return usuarioRepository.findByCorreoUsuario(userDetails.getUsername()).orElseThrow();
    }

    @PostMapping("/evaluar")
    public EvaluarRespuestaResponseDTO evaluar(@Valid @RequestBody EvaluarRespuestaRequestDTO request,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = getUsuario(userDetails);
        return analisisConceptualService.evaluarRespuesta(usuario, request);
    }

    @GetMapping("/perfil")
    public PerfilIADTO perfil(@AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = getUsuario(userDetails);
        return perfilAprendizajeIAService.obtenerPerfilDTO(usuario);
    }

    @GetMapping("/ruta-adaptativa")
    public RutaAdaptativaDTO rutaAdaptativa(@AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = getUsuario(userDetails);
        return rutaAdaptativaService.construirRutaAdaptativa(usuario);
    }

    @GetMapping("/errores")
    public List<ErrorConceptualDTO> errores(@AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = getUsuario(userDetails);
        return errorConceptualService.obtenerErroresRecientes(usuario);
    }

    @PostMapping("/errores/{idError}/resolver")
    public void resolverError(@PathVariable Long idError, @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = getUsuario(userDetails);
        errorConceptualService.marcarResuelto(usuario, idError);
    }
}
