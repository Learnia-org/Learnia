package com.proyecto.Learnia.controller;

import com.proyecto.Learnia.dto.PreguntaDTO;
import com.proyecto.Learnia.service.PreguntaService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

public class PreguntaViewController {
    private final PreguntaService preguntaService;

    public PreguntaViewController(PreguntaService preguntaService) {
        this.preguntaService = preguntaService;
    }


    @GetMapping("/preguntar")
    public String vistaPregunta(Model model) {
        model.addAttribute("preguntaDTO", new PreguntaDTO());
        return "preguntar";
    }

    @PostMapping("/pregunta/crear")
    public String crearPregunta(@ModelAttribute PreguntaDTO dto) {
        preguntaService.guardar(dto);
        return "redirect:/menu";
    }
}
