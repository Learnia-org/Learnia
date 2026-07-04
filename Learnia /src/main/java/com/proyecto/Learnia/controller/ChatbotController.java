package com.proyecto.Learnia.controller;

import com.proyecto.Learnia.dto.ChatRequestDTO;
import com.proyecto.Learnia.dto.ChatResponseDTO;
import com.proyecto.Learnia.service.GeminiService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private final GeminiService geminiService;

    public ChatbotController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/mensaje")
    public ChatResponseDTO enviarMensaje(@Valid @RequestBody ChatRequestDTO request) {
        String respuesta = geminiService.generarRespuesta(request.getMensaje(), request.getHistorial());
        return new ChatResponseDTO(respuesta);
    }
}
