package com.proyecto.Learnia.service;

import com.proyecto.Learnia.dto.PlanEstudioResumenDTO;
import com.proyecto.Learnia.dto.RecordatorioDTO;
import com.proyecto.Learnia.dto.RutaAprendizajeDTO;
import com.proyecto.Learnia.entity.Categoria;
import com.proyecto.Learnia.entity.PlanEstudio;
import com.proyecto.Learnia.entity.PlanEstudioDia;
import com.proyecto.Learnia.entity.Recurso;
import com.proyecto.Learnia.entity.Usuario;
import com.proyecto.Learnia.exception.ResourceNotFoundException;
import com.proyecto.Learnia.repository.CategoriaRepository;
import com.proyecto.Learnia.repository.PlanEstudioDiaRepository;
import com.proyecto.Learnia.repository.PlanEstudioRepository;
import com.proyecto.Learnia.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class PlanEstudioServiceImpl implements PlanEstudioService {

    private static final int MAX_RECORDATORIOS = 6;
    private static final int DIAS_AVISO_EXAMEN = 3;

    private final PlanEstudioRepository planEstudioRepository;
    private final PlanEstudioDiaRepository planEstudioDiaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final RutaAprendizajeService rutaAprendizajeService;

    public PlanEstudioServiceImpl(PlanEstudioRepository planEstudioRepository,
                                  PlanEstudioDiaRepository planEstudioDiaRepository,
                                  UsuarioRepository usuarioRepository,
                                  CategoriaRepository categoriaRepository,
                                  RutaAprendizajeService rutaAprendizajeService) {
        this.planEstudioRepository = planEstudioRepository;
        this.planEstudioDiaRepository = planEstudioDiaRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
        this.rutaAprendizajeService = rutaAprendizajeService;
    }

    @Override
    public PlanEstudio generarPlan(Long idUsuario, Long idCategoria, LocalDate fechaExamen, String titulo) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        if (fechaExamen == null) {
            throw new IllegalArgumentException("Debes indicar la fecha del examen");
        }

        long diasTotal = ChronoUnit.DAYS.between(LocalDate.now(), fechaExamen);
        if (diasTotal < 1) {
            throw new IllegalArgumentException("La fecha del examen debe ser al menos mañana");
        }
        int diasUtiles = (int) Math.min(diasTotal, 90); // límite razonable de planificación

        // 1) Analiza el rendimiento del estudiante y toma los temas pendientes,
        //    priorizando los que nunca vio y luego los que domina menos.
        RutaAprendizajeDTO ruta = rutaAprendizajeService.obtenerRuta(idUsuario, idCategoria);
        List<Recurso> pendientes = new ArrayList<>();
        ruta.getRecursos().forEach(r -> {
            if (!r.isDominado()) {
                pendientes.add(r.getRecurso());
            }
        });

        PlanEstudio plan = new PlanEstudio();
        plan.setUsuario(usuario);
        plan.setCategoria(categoria);
        plan.setFechaExamen(fechaExamen);
        plan.setFechaCreacion(LocalDateTime.now());
        plan.setActivo(true);
        plan.setTitulo((titulo == null || titulo.isBlank())
                ? "Examen de " + categoria.getNombreCategoria()
                : titulo.trim());

        boolean reservarRepasoFinal = diasUtiles > 1;
        int diasContenido = reservarRepasoFinal ? diasUtiles - 1 : diasUtiles;

        List<List<Recurso>> distribucion = distribuirEnBuckets(pendientes, diasContenido);

        List<PlanEstudioDia> dias = new ArrayList<>();
        for (int i = 0; i < diasContenido; i++) {
            int numeroDia = i + 1;
            LocalDate fecha = LocalDate.now().plusDays(numeroDia);
            List<Recurso> recursosDelDia = distribucion.get(i);

            if (recursosDelDia.isEmpty()) {
                dias.add(crearDia(plan, numeroDia, fecha, null, "Repaso libre y práctica"));
            } else {
                for (Recurso recurso : recursosDelDia) {
                    dias.add(crearDia(plan, numeroDia, fecha, recurso, recurso.getTituloRecurso()));
                }
            }
        }

        if (reservarRepasoFinal) {
            dias.add(crearDia(plan, diasUtiles, fechaExamen, null, "Repaso final antes del examen"));
        }

        plan.setDias(dias);
        return planEstudioRepository.save(plan);
    }

    private PlanEstudioDia crearDia(PlanEstudio plan, int numeroDia, LocalDate fecha, Recurso recurso, String titulo) {
        PlanEstudioDia dia = new PlanEstudioDia();
        dia.setPlanEstudio(plan);
        dia.setNumeroDia(numeroDia);
        dia.setFecha(fecha);
        dia.setRecurso(recurso);
        dia.setTituloTema(titulo);
        dia.setCompletado(false);
        return dia;
    }

    /**
     * Reparte una lista de elementos en N "cubetas" (días) de la forma más
     * pareja posible. Si hay más días que elementos, algunas cubetas quedan vacías.
     */
    private <T> List<List<T>> distribuirEnBuckets(List<T> items, int buckets) {
        List<List<T>> resultado = new ArrayList<>();
        if (buckets <= 0) {
            return resultado;
        }
        int size = items.size();
        int base = size / buckets;
        int extra = size % buckets;
        int indice = 0;
        for (int i = 0; i < buckets; i++) {
            int cantidad = base + (i < extra ? 1 : 0);
            List<T> bucket = new ArrayList<>(items.subList(indice, indice + cantidad));
            resultado.add(bucket);
            indice += cantidad;
        }
        return resultado;
    }

    @Override
    public List<PlanEstudioResumenDTO> obtenerPlanesUsuario(Long idUsuario) {
        List<PlanEstudio> planes = planEstudioRepository.findByUsuario_IdUsuarioOrderByFechaCreacionDesc(idUsuario);
        List<PlanEstudioResumenDTO> resumen = new ArrayList<>();

        for (PlanEstudio plan : planes) {
            long total = planEstudioDiaRepository.countByPlanEstudio_IdPlan(plan.getIdPlan());
            long completadas = planEstudioDiaRepository.countByPlanEstudio_IdPlanAndCompletadoTrue(plan.getIdPlan());
            int porcentaje = total == 0 ? 0 : (int) ((completadas * 100) / total);
            long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), plan.getFechaExamen());
            boolean finalizado = plan.getFechaExamen().isBefore(LocalDate.now());

            resumen.add(new PlanEstudioResumenDTO(plan, diasRestantes, total, completadas, porcentaje, finalizado));
        }
        return resumen;
    }

    @Override
    public PlanEstudio obtenerPlan(Long idPlan, Long idUsuario) {
        PlanEstudio plan = planEstudioRepository.findById(idPlan)
                .orElseThrow(() -> new ResourceNotFoundException("Plan de estudio no encontrado"));

        if (!plan.getUsuario().getIdUsuario().equals(idUsuario)) {
            throw new ResourceNotFoundException("Plan de estudio no encontrado");
        }
        return plan;
    }

    @Override
    public List<PlanEstudioDia> obtenerDias(Long idPlan) {
        return planEstudioDiaRepository.findByPlanEstudio_IdPlanOrderByNumeroDiaAscIdDiaAsc(idPlan);
    }

    @Override
    public void marcarDiaCompletado(Long idDia, Long idUsuario, boolean completado) {
        PlanEstudioDia dia = planEstudioDiaRepository.findById(idDia)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea del plan no encontrada"));

        if (!dia.getPlanEstudio().getUsuario().getIdUsuario().equals(idUsuario)) {
            throw new ResourceNotFoundException("Tarea del plan no encontrada");
        }

        dia.setCompletado(completado);
        planEstudioDiaRepository.save(dia);
    }

    @Override
    public void eliminarPlan(Long idPlan, Long idUsuario) {
        PlanEstudio plan = obtenerPlan(idPlan, idUsuario);
        planEstudioRepository.delete(plan);
    }

    @Override
    public List<RecordatorioDTO> obtenerRecordatorios(Long idUsuario) {
        List<RecordatorioDTO> recordatorios = new ArrayList<>();
        LocalDate hoy = LocalDate.now();

        List<PlanEstudioDia> pendientes = planEstudioDiaRepository
                .findByPlanEstudio_Usuario_IdUsuarioAndFechaLessThanEqualAndCompletadoFalseAndPlanEstudio_ActivoTrueOrderByFechaAsc(
                        idUsuario, hoy);

        for (PlanEstudioDia dia : pendientes) {
            if (recordatorios.size() >= MAX_RECORDATORIOS) break;
            boolean atrasado = dia.getFecha().isBefore(hoy);
            String nombreCategoria = dia.getPlanEstudio().getCategoria().getNombreCategoria();
            String mensaje = (atrasado ? "Atrasado: " : "Hoy toca: ") + dia.getTituloTema() + " (" + nombreCategoria + ")";
            recordatorios.add(new RecordatorioDTO(mensaje, atrasado ? "atrasado" : "hoy", dia.getPlanEstudio().getIdPlan()));
        }

        List<PlanEstudio> planesActivos = planEstudioRepository.findByUsuario_IdUsuarioAndActivoTrue(idUsuario);
        for (PlanEstudio plan : planesActivos) {
            if (recordatorios.size() >= MAX_RECORDATORIOS) break;
            long diasRestantes = ChronoUnit.DAYS.between(hoy, plan.getFechaExamen());
            if (diasRestantes >= 0 && diasRestantes <= DIAS_AVISO_EXAMEN) {
                String texto = diasRestantes == 0
                        ? "¡Tu examen de " + plan.getCategoria().getNombreCategoria() + " es hoy!"
                        : "Tu examen de " + plan.getCategoria().getNombreCategoria() + " es en " + diasRestantes
                        + (diasRestantes == 1 ? " día" : " días");
                recordatorios.add(new RecordatorioDTO(texto, "examen", plan.getIdPlan()));
            }
        }

        return recordatorios;
    }
}