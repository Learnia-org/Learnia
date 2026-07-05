package com.proyecto.Learnia.service;

import com.proyecto.Learnia.dto.PerfilIADTO;
import com.proyecto.Learnia.entity.ErrorConceptual;
import com.proyecto.Learnia.entity.PerfilAprendizajeIA;
import com.proyecto.Learnia.entity.Usuario;
import com.proyecto.Learnia.repository.ErrorConceptualRepository;
import com.proyecto.Learnia.repository.PerfilAprendizajeIARepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PerfilAprendizajeIAServiceImpl implements PerfilAprendizajeIAService {

    private final PerfilAprendizajeIARepository perfilRepository;
    private final ErrorConceptualRepository errorConceptualRepository;
    private final GeminiService geminiService;

    public PerfilAprendizajeIAServiceImpl(PerfilAprendizajeIARepository perfilRepository,
                                           ErrorConceptualRepository errorConceptualRepository,
                                           GeminiService geminiService) {
        this.perfilRepository = perfilRepository;
        this.errorConceptualRepository = errorConceptualRepository;
        this.geminiService = geminiService;
    }

    @Override
    public PerfilAprendizajeIA obtenerOCrearPerfil(Usuario usuario) {
        return perfilRepository.findByUsuario_IdUsuario(usuario.getIdUsuario())
                .orElseGet(() -> perfilRepository.save(new PerfilAprendizajeIA(usuario)));
    }

    @Override
    public PerfilIADTO obtenerPerfilDTO(Usuario usuario) {
        PerfilAprendizajeIA perfil = obtenerOCrearPerfil(usuario);
        return convertir(perfil);
    }

    @Override
    public void registrarResultadoEjercicio(Usuario usuario, boolean correcta) {
        PerfilAprendizajeIA perfil = obtenerOCrearPerfil(usuario);
        perfil.setTotalEjerciciosIntentados(perfil.getTotalEjerciciosIntentados() + 1);
        if (correcta) {
            perfil.setTotalEjerciciosCorrectos(perfil.getTotalEjerciciosCorrectos() + 1);
        } else {
            perfil.setTotalErroresDetectados(perfil.getTotalErroresDetectados() + 1);
        }
        int intentos = perfil.getTotalEjerciciosIntentados();
        perfil.setNivelConfianza(Math.min(100, intentos * 5));
        perfil.setFechaActualizacion(LocalDateTime.now());
        perfilRepository.save(perfil);
    }

    @Override
    public void actualizarPerfilConIA(Usuario usuario) {
        PerfilAprendizajeIA perfil = obtenerOCrearPerfil(usuario);

        String contextoEstadisticas = """
                Ejercicios intentados: %d
                Ejercicios correctos: %d
                Errores conceptuales totales detectados: %d
                """.formatted(
                perfil.getTotalEjerciciosIntentados(),
                perfil.getTotalEjerciciosCorrectos(),
                perfil.getTotalErroresDetectados()
        );

        List<ErrorConceptual> ultimosErrores = errorConceptualRepository
                .findTop10ByUsuario_IdUsuarioOrderByFechaDeteccionDesc(usuario.getIdUsuario());

        String contextoErrores = ultimosErrores.isEmpty()
                ? "No se registran errores conceptuales todavía."
                : ultimosErrores.stream()
                    .map(e -> "- Tema: " + (e.getTema() != null ? e.getTema() : "General")
                            + " | Error: " + e.getDescripcionError())
                    .collect(Collectors.joining("\n"));

        GeminiService.ResultadoPerfil resultado = geminiService.generarPerfilAprendizaje(contextoEstadisticas, contextoErrores);

        perfil.setFortalezas(resultado.fortalezas());
        perfil.setDebilidades(resultado.debilidades());
        perfil.setResumenIA(resultado.resumen());
        perfil.setEstiloAprendizaje(parseEstilo(resultado.estilo()));
        perfil.setRitmoAprendizaje(parseRitmo(resultado.ritmo()));
        perfil.setFechaActualizacion(LocalDateTime.now());

        perfilRepository.save(perfil);
    }

    private com.proyecto.Learnia.entity.EstiloAprendizaje parseEstilo(String valor) {
        try {
            return com.proyecto.Learnia.entity.EstiloAprendizaje.valueOf(valor.trim().toUpperCase());
        } catch (Exception e) {
            return com.proyecto.Learnia.entity.EstiloAprendizaje.LECTOESCRITURA;
        }
    }

    private com.proyecto.Learnia.entity.RitmoAprendizaje parseRitmo(String valor) {
        try {
            return com.proyecto.Learnia.entity.RitmoAprendizaje.valueOf(valor.trim().toUpperCase());
        } catch (Exception e) {
            return com.proyecto.Learnia.entity.RitmoAprendizaje.MODERADO;
        }
    }

    private PerfilIADTO convertir(PerfilAprendizajeIA perfil) {
        return new PerfilIADTO(
                perfil.getUsuario().getIdUsuario(),
                perfil.getUsuario().getNombreUsuario(),
                perfil.getFortalezas(),
                perfil.getDebilidades(),
                perfil.getEstiloAprendizaje(),
                perfil.getRitmoAprendizaje(),
                perfil.getResumenIA(),
                perfil.getNivelConfianza(),
                perfil.getTotalErroresDetectados(),
                perfil.getTotalEjerciciosIntentados(),
                perfil.getTotalEjerciciosCorrectos(),
                perfil.getFechaActualizacion()
        );
    }
}
