package com.proyecto.Learnia.config;

import com.proyecto.Learnia.entity.RolUsuario;
import com.proyecto.Learnia.entity.Usuario;
import com.proyecto.Learnia.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class BotUsuarioInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public BotUsuarioInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private static final String IA_RESPUESTAS_CORREO = "asistente.ia@learnia.com";

    @Override
    public void run(String... args) {
        corregirUsuarioIaDeRespuestas();

        if (!usuarioRepository.findByEsBotTrue().isEmpty()) {
            return;
        }

        Usuario asistente = new Usuario();
        asistente.setNombreUsuario("Asistente IA");
        asistente.setCorreoUsuario("asistente.ia@learnia.app");
        asistente.setContrasenaUsuario(passwordEncoder.encode(UUID.randomUUID().toString()));
        asistente.setFechaRegistro(LocalDateTime.now());
        asistente.setRolUsuario(RolUsuario.ESTUDIANTE);
        asistente.setEsBot(true);
        asistente.setEnLinea(true);
        asistente.setUltimoAcceso(LocalDateTime.now());
        usuarioRepository.save(asistente);
    }

    private void corregirUsuarioIaDeRespuestas() {
        java.util.List<Usuario> botsExistentes = usuarioRepository.findByEsBotTrue();
        usuarioRepository.findByCorreoUsuario(IA_RESPUESTAS_CORREO).ifPresent(usuario -> {
            boolean hayOtroBotDistinto = botsExistentes.stream()
                    .anyMatch(b -> !b.getIdUsuario().equals(usuario.getIdUsuario()));
            if (!usuario.isEsBot() && !hayOtroBotDistinto) {
                usuario.setEsBot(true);
                usuarioRepository.save(usuario);
            }
        });
    }
}
