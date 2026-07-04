package com.proyecto.Learnia.service;

import com.proyecto.Learnia.entity.Pregunta;
import com.proyecto.Learnia.entity.Respuesta;
import com.proyecto.Learnia.entity.RolUsuario;
import com.proyecto.Learnia.entity.Usuario;
import com.proyecto.Learnia.repository.PreguntaRepository;
import com.proyecto.Learnia.repository.RespuestaRepository;

import com.proyecto.Learnia.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RespuestaServiceImpl implements RespuestaService {

    private static final String IA_CORREO = "asistente.ia@learnia.com";
    private static final String IA_NOMBRE = "Asistente Learnia";

    private final RespuestaRepository respuestaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PreguntaRepository preguntaRepository;
    private final PasswordEncoder passwordEncoder;
    private final GeminiService geminiService;

    public RespuestaServiceImpl(
            RespuestaRepository respuestaRepository,
            UsuarioRepository usuarioRepository,
            PreguntaRepository preguntaRepository,
            PasswordEncoder passwordEncoder,
            GeminiService geminiService) {

        this.respuestaRepository = respuestaRepository;
        this.usuarioRepository = usuarioRepository;
        this.preguntaRepository = preguntaRepository;
        this.passwordEncoder = passwordEncoder;
        this.geminiService = geminiService;
    }

    @Override
    public List<Respuesta> listar() {
        return respuestaRepository.findAll();
    }

    public Respuesta guardar(Long preguntaId, String contenido) {

        Pregunta pregunta = preguntaRepository.findById(preguntaId)
                .orElseThrow(() ->
                        new RuntimeException("Pregunta no encontrada")
                );

        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        String correo = auth.getName();

        Usuario usuario = usuarioRepository.findByCorreoUsuario(correo)
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado")
                );

        Respuesta respuesta = new Respuesta();

        respuesta.setContenido(contenido);
        respuesta.setFechaRespuesta(LocalDateTime.now());
        respuesta.setPregunta(pregunta);
        respuesta.setUsuario(usuario);
        respuesta.setOculta(geminiService.contieneContenidoProhibido(contenido)
                || !geminiService.esContenidoCoherente(contenido));

        return respuestaRepository.save(respuesta);
    }

    @Override
    public Respuesta guardarComoIA(Long preguntaId, String contenido) {

        Pregunta pregunta = preguntaRepository.findById(preguntaId)
                .orElseThrow(() ->
                        new RuntimeException("Pregunta no encontrada")
                );

        Usuario usuarioIA = obtenerOCrearUsuarioIA();

        Respuesta respuesta = new Respuesta();

        respuesta.setContenido(contenido);
        respuesta.setFechaRespuesta(LocalDateTime.now());
        respuesta.setPregunta(pregunta);
        respuesta.setUsuario(usuarioIA);

        return respuestaRepository.save(respuesta);
    }

    private Usuario obtenerOCrearUsuarioIA() {
        return usuarioRepository.findByCorreoUsuario(IA_CORREO)
                .orElseGet(() -> {
                    Usuario usuarioIA = new Usuario();
                    usuarioIA.setNombreUsuario(IA_NOMBRE);
                    usuarioIA.setCorreoUsuario(IA_CORREO);
                    usuarioIA.setContrasenaUsuario(passwordEncoder.encode(UUID.randomUUID().toString()));
                    usuarioIA.setFechaRegistro(LocalDateTime.now());
                    usuarioIA.setRolUsuario(RolUsuario.ESTUDIANTE);
                    usuarioIA.setBloqueado(true);
                    return usuarioRepository.save(usuarioIA);
                });
    }

    @Override
    public Respuesta buscarPorId(Long id) {

        return respuestaRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Respuesta no encontrada")
                );
    }

    @Override
    public Respuesta actualizar(Long id, Respuesta nuevaRespuesta) {

        Respuesta existente = buscarPorId(id);

        existente.setContenido(nuevaRespuesta.getContenido());

        return respuestaRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        respuestaRepository.deleteById(id);
    }
}
