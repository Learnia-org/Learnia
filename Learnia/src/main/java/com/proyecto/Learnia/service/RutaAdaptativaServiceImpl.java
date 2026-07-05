package com.proyecto.Learnia.service;

import com.proyecto.Learnia.dto.RecomendacionRutaDTO;
import com.proyecto.Learnia.dto.RutaAdaptativaDTO;
import com.proyecto.Learnia.dto.RutaResumenDTO;
import com.proyecto.Learnia.entity.Recurso;
import com.proyecto.Learnia.entity.Usuario;
import com.proyecto.Learnia.repository.ErrorConceptualRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class RutaAdaptativaServiceImpl implements RutaAdaptativaService {

    private static final Map<String, Integer> ORDEN_PRIORIDAD = Map.of("ALTA", 0, "MEDIA", 1, "BAJA", 2);

    private final RutaAprendizajeService rutaAprendizajeService;
    private final ErrorConceptualRepository errorConceptualRepository;
    private final PerfilAprendizajeIAService perfilAprendizajeIAService;

    public RutaAdaptativaServiceImpl(RutaAprendizajeService rutaAprendizajeService,
                                      ErrorConceptualRepository errorConceptualRepository,
                                      PerfilAprendizajeIAService perfilAprendizajeIAService) {
        this.rutaAprendizajeService = rutaAprendizajeService;
        this.errorConceptualRepository = errorConceptualRepository;
        this.perfilAprendizajeIAService = perfilAprendizajeIAService;
    }

    @Override
    public RutaAdaptativaDTO construirRutaAdaptativa(Usuario usuario) {
        List<RutaResumenDTO> resumenGeneral = rutaAprendizajeService.obtenerResumenGeneral(usuario.getIdUsuario());
        List<RecomendacionRutaDTO> recomendaciones = new ArrayList<>();

        for (RutaResumenDTO resumen : resumenGeneral) {
            Long idCategoria = resumen.getCategoria().getIdCategoria();
            long erroresSinResolver = errorConceptualRepository
                    .countByUsuario_IdUsuarioAndCategoria_IdCategoriaAndResueltoFalse(usuario.getIdUsuario(), idCategoria);

            String prioridad;
            String motivo;

            if (erroresSinResolver > 0) {
                prioridad = "ALTA";
                motivo = "Tenés " + erroresSinResolver + " error(es) conceptual(es) sin resolver en esta materia.";
            } else if (resumen.getPorcentaje() < 50) {
                prioridad = "ALTA";
                motivo = "Tu dominio en esta materia es bajo (" + resumen.getPorcentaje() + "%). Conviene reforzarla ahora.";
            } else if (resumen.getPorcentaje() < 80) {
                prioridad = "MEDIA";
                motivo = "Vas bien (" + resumen.getPorcentaje() + "%), pero todavía hay recursos por dominar.";
            } else {
                prioridad = "BAJA";
                motivo = "Excelente dominio (" + resumen.getPorcentaje() + "%). Podés avanzar a otro tema o repasar de vez en cuando.";
            }

            Recurso siguiente = resumen.getSiguienteRecomendado();

            recomendaciones.add(new RecomendacionRutaDTO(
                    idCategoria,
                    resumen.getCategoria().getNombreCategoria(),
                    prioridad,
                    motivo,
                    resumen.getPorcentaje(),
                    siguiente != null ? siguiente.getIdRecurso() : null,
                    siguiente != null ? siguiente.getTituloRecurso() : null
            ));
        }

        recomendaciones.sort(Comparator.comparingInt(r -> ORDEN_PRIORIDAD.getOrDefault(r.getPrioridad(), 3)));

        return new RutaAdaptativaDTO(recomendaciones, perfilAprendizajeIAService.obtenerPerfilDTO(usuario));
    }
}
