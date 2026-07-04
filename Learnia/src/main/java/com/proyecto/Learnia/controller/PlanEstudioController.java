package com.proyecto.Learnia.controller;

import com.proyecto.Learnia.entity.Categoria;
import com.proyecto.Learnia.entity.PlanEstudio;
import com.proyecto.Learnia.entity.Usuario;
import com.proyecto.Learnia.repository.CategoriaRepository;
import com.proyecto.Learnia.repository.UsuarioRepository;
import com.proyecto.Learnia.service.PlanEstudioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/plan-estudio")
public class PlanEstudioController {

    private final PlanEstudioService planEstudioService;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;

    public PlanEstudioController(PlanEstudioService planEstudioService,
                                 UsuarioRepository usuarioRepository,
                                 CategoriaRepository categoriaRepository) {
        this.planEstudioService = planEstudioService;
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
    }

    private Usuario getUsuario(UserDetails userDetails) {
        return usuarioRepository.findByCorreoUsuario(userDetails.getUsername()).orElseThrow();
    }

    @GetMapping
    public String listar(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = getUsuario(userDetails);
        model.addAttribute("usuario", usuario);
        model.addAttribute("planes", planEstudioService.obtenerPlanesUsuario(usuario.getIdUsuario()));
        model.addAttribute("recordatorios", planEstudioService.obtenerRecordatorios(usuario.getIdUsuario()));
        return "plan-estudio";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = getUsuario(userDetails);
        model.addAttribute("usuario", usuario);
        model.addAttribute("categorias", categoriaRepository.findByActivaTrue());
        return "plan-estudio-nuevo";
    }

    @PostMapping("/generar")
    public String generar(@RequestParam Long idCategoria,
                          @RequestParam String fechaExamen,
                          @RequestParam(required = false) String titulo,
                          @AuthenticationPrincipal UserDetails userDetails,
                          Model model) {
        Usuario usuario = getUsuario(userDetails);
        try {
            LocalDate fecha = LocalDate.parse(fechaExamen);
            PlanEstudio plan = planEstudioService.generarPlan(usuario.getIdUsuario(), idCategoria, fecha, titulo);
            return "redirect:/plan-estudio/" + plan.getIdPlan();
        } catch (IllegalArgumentException e) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("categorias", categoriaRepository.findByActivaTrue());
            model.addAttribute("error", e.getMessage());
            return "plan-estudio-nuevo";
        }
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = getUsuario(userDetails);
        PlanEstudio plan = planEstudioService.obtenerPlan(id, usuario.getIdUsuario());

        model.addAttribute("usuario", usuario);
        model.addAttribute("plan", plan);
        model.addAttribute("dias", planEstudioService.obtenerDias(id));
        return "plan-estudio-detalle";
    }

    @PostMapping("/dia/{idDia}/completar")
    public String completarDia(@PathVariable Long idDia,
                               @RequestParam Long idPlan,
                               @RequestParam(defaultValue = "true") boolean completado,
                               @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = getUsuario(userDetails);
        planEstudioService.marcarDiaCompletado(idDia, usuario.getIdUsuario(), completado);
        return "redirect:/plan-estudio/" + idPlan;
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = getUsuario(userDetails);
        planEstudioService.eliminarPlan(id, usuario.getIdUsuario());
        return "redirect:/plan-estudio";
    }
}