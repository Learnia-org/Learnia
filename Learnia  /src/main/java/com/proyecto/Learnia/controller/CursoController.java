package com.proyecto.Learnia.controller;

import com.proyecto.Learnia.entity.Usuario;
import com.proyecto.Learnia.repository.RecursoRepository;
import com.proyecto.Learnia.repository.UsuarioRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/materias")
public class CursoController {

    private final RecursoRepository recursoRepository;
    private final UsuarioRepository usuarioRepository;

    public CursoController(RecursoRepository recursoRepository,
                           UsuarioRepository usuarioRepository) {
        this.recursoRepository = recursoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private Usuario getUsuario(UserDetails userDetails) {
        return usuarioRepository.findByCorreoUsuario(userDetails.getUsername()).orElseThrow();
    }

    @GetMapping("/matematica")
    public String matematica(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("recursos", recursoRepository.findByCategoria_IdCategoria(1L));
        model.addAttribute("usuario", getUsuario(userDetails));
        return "cursos/matematica";
    }

    @GetMapping("/fisica")
    public String fisica(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("recursos", recursoRepository.findByCategoria_IdCategoria(2L));
        model.addAttribute("usuario", getUsuario(userDetails));
        return "cursos/fisica";
    }

    @GetMapping("/informatica")
    public String informatica(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("recursos", recursoRepository.findByCategoria_IdCategoria(3L));
        model.addAttribute("usuario", getUsuario(userDetails));
        return "cursos/informatica";
    }

    @GetMapping("/ingles")
    public String ingles(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("recursos", recursoRepository.findByCategoria_IdCategoria(4L));
        model.addAttribute("usuario", getUsuario(userDetails));
        return "cursos/ingles";
    }
}