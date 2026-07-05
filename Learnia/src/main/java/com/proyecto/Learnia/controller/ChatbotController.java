package com.proyecto.Learnia.controller;

import com.proyecto.Learnia.dto.ChatMensajeDTO;
import com.proyecto.Learnia.dto.ChatRequestDTO;
import com.proyecto.Learnia.dto.ChatResponseDTO;
import com.proyecto.Learnia.dto.MemoriaMensajeDTO;
import com.proyecto.Learnia.entity.Usuario;
import com.proyecto.Learnia.repository.UsuarioRepository;
import com.proyecto.Learnia.service.GeminiService;
import com.proyecto.Learnia.service.MemoriaConversacionalService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private final GeminiService geminiService;
    private final MemoriaConversacionalService memoriaConversacionalService;
    private final UsuarioRepository usuarioRepository;

    public ChatbotController(GeminiService geminiService,
                              MemoriaConversacionalService memoriaConversacionalService,
                              UsuarioRepository usuarioRepository) {
        this.geminiService = geminiService;
        this.memoriaConversacionalService = memoriaConversacionalService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/mensaje")
    public ChatResponseDTO enviarMensaje(@Valid @RequestBody ChatRequestDTO request) {
        String respuesta = geminiService.generarRespuesta(request.getMensaje(), request.getHistorial());

        // Memoria del estudiante: si hay un usuario autenticado, se guarda el
        // intercambio para poder recordarlo en futuras sesiones o dispositivos.
        usuarioActual().ifPresent(usuario -> {
            memoriaConversacionalService.guardarMensaje(usuario, "user", request.getMensaje());
            memoriaConversacionalService.guardarMensaje(usuario, "model", respuesta);
        });

        return new ChatResponseDTO(respuesta);
    }

    /**
     * Devuelve la memoria de conversación guardada del estudiante autenticado,
     * usada por el widget de chat para restaurar el contexto si el navegador
     * no conserva el historial local (otro dispositivo, caché borrada, etc.).
     */
    @GetMapping("/historial")
    public List<ChatMensajeDTO> obtenerHistorial() {
        return usuarioActual()
                .map(usuario -> memoriaConversacionalService.obtenerHistorialReciente(usuario, 40))
                .orElseGet(List::of)
                .stream()
                .map(this::convertir)
                .toList();
    }

    private ChatMensajeDTO convertir(MemoriaMensajeDTO memoria) {
        ChatMensajeDTO dto = new ChatMensajeDTO();
        dto.setRole(memoria.getRole());
        dto.setTexto(memoria.getTexto());
        return dto;
    }

    private Optional<Usuario> usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return Optional.empty();
        }
        return usuarioRepository.findByCorreoUsuario(auth.getName());
    }
}
