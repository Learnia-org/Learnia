package com.proyecto.Learnia.controller;

import com.proyecto.Learnia.entity.Categoria;
import com.proyecto.Learnia.entity.Pregunta;
import com.proyecto.Learnia.service.CategoriaService;
import com.proyecto.Learnia.service.PreguntaService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cursos")
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final PreguntaService preguntaService;

    public CategoriaController(
            CategoriaService categoriaService,
            PreguntaService preguntaService
    ) {
        this.categoriaService = categoriaService;
        this.preguntaService = preguntaService;
    }

    @GetMapping
    public String verCursos(Model model) {
        List<Categoria> categorias = categoriaService.listar();
        model.addAttribute("categorias", categorias);
        model.addAttribute("titulo", "Todos los Cursos");
        return "cursos";
    }

    @GetMapping("/categoria/{id}")
    public String verCategoria(@PathVariable Long id, Model model) {
        Categoria categoria = categoriaService.buscarPorId(id);
        List<Pregunta> preguntas = preguntaService.buscarPorCategoria(id);
        model.addAttribute("categoria", categoria);
        model.addAttribute("preguntas", preguntas);
        model.addAttribute("titulo", categoria.getNombreCategoria());
        return "curso-detalle";
    }

    @GetMapping("/matematica")
    public String matematica() { return "cursos/matematica"; }

    @GetMapping("/fisica")
    public String fisica() { return "cursos/fisica"; }

    @GetMapping("/ingles")
    public String ingles() { return "cursos/ingles"; }

    @GetMapping("/informatica")
    public String informatica() { return "cursos/informatica"; }
}