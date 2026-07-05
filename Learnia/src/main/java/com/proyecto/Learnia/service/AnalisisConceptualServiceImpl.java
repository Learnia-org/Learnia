package com.proyecto.Learnia.service;

import com.proyecto.Learnia.dto.EvaluarRespuestaRequestDTO;
import com.proyecto.Learnia.dto.EvaluarRespuestaResponseDTO;
import com.proyecto.Learnia.entity.Categoria;
import com.proyecto.Learnia.entity.ErrorConceptual;
import com.proyecto.Learnia.entity.IntentoEjercicio;
import com.proyecto.Learnia.entity.OrigenError;
import com.proyecto.Learnia.entity.Recurso;
import com.proyecto.Learnia.entity.Usuario;
import com.proyecto.Learnia.repository.CategoriaRepository;
import com.proyecto.Learnia.repository.ErrorConceptualRepository;
import com.proyecto.Learnia.repository.IntentoEjercicioRepository;
import com.proyecto.Learnia.repository.RecursoRepository;
import org.springframework.stereotype.Service;

@Service
public class AnalisisConceptualServiceImpl implements AnalisisConceptualService {

    private final GeminiService geminiService;
    private final IntentoEjercicioRepository intentoEjercicioRepository;
    private final ErrorConceptualRepository errorConceptualRepository;
    private final CategoriaRepository categoriaRepository;
    private final RecursoRepository recursoRepository;
    private final PerfilAprendizajeIAService perfilAprendizajeIAService;

    public AnalisisConceptualServiceImpl(GeminiService geminiService,
                                          IntentoEjercicioRepository intentoEjercicioRepository,
                                          ErrorConceptualRepository errorConceptualRepository,
                                          CategoriaRepository categoriaRepository,
                                          RecursoRepository recursoRepository,
                                          PerfilAprendizajeIAService perfilAprendizajeIAService) {
        this.geminiService = geminiService;
        this.intentoEjercicioRepository = intentoEjercicioRepository;
        this.errorConceptualRepository = errorConceptualRepository;
        this.categoriaRepository = categoriaRepository;
        this.recursoRepository = recursoRepository;
        this.perfilAprendizajeIAService = perfilAprendizajeIAService;
    }

    @Override
    public EvaluarRespuestaResponseDTO evaluarRespuesta(Usuario usuario, EvaluarRespuestaRequestDTO request) {
        GeminiService.ResultadoEvaluacion resultado =
                geminiService.evaluarRespuestaEjercicio(request.getEnunciado(), request.getRespuesta());

        Categoria categoria = request.getIdCategoria() != null
                ? categoriaRepository.findById(request.getIdCategoria()).orElse(null)
                : null;
        Recurso recurso = request.getIdRecurso() != null
                ? recursoRepository.findById(request.getIdRecurso()).orElse(null)
                : null;

        IntentoEjercicio intento = new IntentoEjercicio();
        intento.setUsuario(usuario);
        intento.setCategoria(categoria);
        intento.setRecurso(recurso);
        intento.setEnunciado(request.getEnunciado());
        intento.setRespuestaEstudiante(request.getRespuesta());
        intento.setEsCorrecta(resultado.correcta());
        intento.setTipoErrorConceptual(resultado.tipoError());
        intento.setExplicacionIA(resultado.explicacion());
        intento.setSugerenciaIA(resultado.sugerencia());
        intentoEjercicioRepository.save(intento);

        perfilAprendizajeIAService.registrarResultadoEjercicio(usuario, resultado.correcta());

        if (!resultado.correcta()) {
            ErrorConceptual error = new ErrorConceptual();
            error.setUsuario(usuario);
            error.setCategoria(categoria);
            error.setTema(resultado.tipoError() != null ? resultado.tipoError() : recortar(request.getEnunciado()));
            error.setOrigen(OrigenError.EJERCICIO);
            error.setDescripcionError("Respuesta del estudiante: " + request.getRespuesta());
            error.setExplicacionIA(resultado.explicacion() + " Sugerencia: " + resultado.sugerencia());
            errorConceptualRepository.save(error);
        }

        // Recalcula el perfil con IA cada pocos intentos para mantenerlo actualizado
        // sin saturar la API externa en cada envío.
        long totalIntentos = intentoEjercicioRepository.countByUsuario_IdUsuario(usuario.getIdUsuario());
        if (totalIntentos <= 1 || totalIntentos % 3 == 0) {
            perfilAprendizajeIAService.actualizarPerfilConIA(usuario);
        }

        return new EvaluarRespuestaResponseDTO(
                resultado.correcta(),
                resultado.tipoError(),
                resultado.explicacion(),
                resultado.sugerencia(),
                perfilAprendizajeIAService.obtenerPerfilDTO(usuario)
        );
    }

    private String recortar(String texto) {
        if (texto == null) return "Tema general";
        return texto.length() <= 100 ? texto : texto.substring(0, 100) + "...";
    }
}
