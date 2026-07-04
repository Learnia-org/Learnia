package com.proyecto.Learnia.service;

import com.proyecto.Learnia.dto.RecursoRutaDTO;
import com.proyecto.Learnia.dto.RutaAprendizajeDTO;
import com.proyecto.Learnia.dto.RutaResumenDTO;
import com.proyecto.Learnia.entity.Categoria;
import com.proyecto.Learnia.entity.Recurso;
import com.proyecto.Learnia.entity.RecursoProgreso;
import com.proyecto.Learnia.entity.Usuario;
import com.proyecto.Learnia.exception.ResourceNotFoundException;
import com.proyecto.Learnia.repository.CategoriaRepository;
import com.proyecto.Learnia.repository.RecursoProgresoRepository;
import com.proyecto.Learnia.repository.RecursoRepository;
import com.proyecto.Learnia.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RutaAprendizajeServiceImpl implements RutaAprendizajeService {

    private final RecursoRepository recursoRepository;
    private final RecursoProgresoRepository recursoProgresoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;

    public RutaAprendizajeServiceImpl(RecursoRepository recursoRepository,
                                      RecursoProgresoRepository recursoProgresoRepository,
                                      CategoriaRepository categoriaRepository,
                                      UsuarioRepository usuarioRepository) {
        this.recursoRepository = recursoRepository;
        this.recursoProgresoRepository = recursoProgresoRepository;
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public RutaAprendizajeDTO obtenerRuta(Long idUsuario, Long idCategoria) {
        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        List<RecursoRutaDTO> recursosRuta = construirListaOrdenada(idUsuario, idCategoria);

        int total = recursosRuta.size();
        int dominados = (int) recursosRuta.stream().filter(RecursoRutaDTO::isDominado).count();
        int porcentaje = total == 0 ? 0 : (dominados * 100) / total;

        RecursoRutaDTO recomendado = recursosRuta.stream()
                .filter(r -> !r.isDominado())
                .findFirst()
                .orElse(null);

        return new RutaAprendizajeDTO(categoria, recursosRuta, recomendado, total, dominados, porcentaje);
    }

    @Override
    public List<RutaResumenDTO> obtenerResumenGeneral(Long idUsuario) {
        List<Categoria> categorias = categoriaRepository.findByActivaTrue();
        List<RutaResumenDTO> resumen = new ArrayList<>();

        for (Categoria categoria : categorias) {
            List<RecursoRutaDTO> recursosRuta = construirListaOrdenada(idUsuario, categoria.getIdCategoria());
            int total = recursosRuta.size();
            int dominados = (int) recursosRuta.stream().filter(RecursoRutaDTO::isDominado).count();
            int porcentaje = total == 0 ? 0 : (dominados * 100) / total;

            Recurso siguiente = recursosRuta.stream()
                    .filter(r -> !r.isDominado())
                    .map(RecursoRutaDTO::getRecurso)
                    .findFirst()
                    .orElse(null);

            resumen.add(new RutaResumenDTO(categoria, total, dominados, porcentaje, siguiente));
        }
        return resumen;
    }

    @Override
    public void marcarProgreso(Long idUsuario, Long idRecurso, boolean completado, int nivelDominio) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        Recurso recurso = recursoRepository.findById(idRecurso)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso no encontrado"));

        int nivelValidado = Math.max(0, Math.min(5, nivelDominio));

        RecursoProgreso progreso = recursoProgresoRepository
                .findByUsuario_IdUsuarioAndRecurso_IdRecurso(idUsuario, idRecurso)
                .orElseGet(() -> {
                    RecursoProgreso nuevo = new RecursoProgreso();
                    nuevo.setUsuario(usuario);
                    nuevo.setRecurso(recurso);
                    return nuevo;
                });

        progreso.setCompletado(completado);
        progreso.setNivelDominio(nivelValidado);
        progreso.setFechaActualizacion(LocalDateTime.now());

        recursoProgresoRepository.save(progreso);
    }

    /**
     * Arma la lista de recursos de una categoría ordenada según prioridad de estudio:
     * 1) temas nunca vistos (en el orden en que se subieron)
     * 2) temas vistos pero con bajo nivel de dominio (los más débiles primero)
     * 3) temas ya dominados, al final
     */
    private List<RecursoRutaDTO> construirListaOrdenada(Long idUsuario, Long idCategoria) {
        List<Recurso> recursos = recursoRepository.findByCategoria_IdCategoria(idCategoria);

        Map<Long, RecursoProgreso> progresoPorRecurso = recursoProgresoRepository
                .findByUsuario_IdUsuarioAndRecurso_Categoria_IdCategoria(idUsuario, idCategoria)
                .stream()
                .collect(Collectors.toMap(p -> p.getRecurso().getIdRecurso(), p -> p));

        List<RecursoRutaDTO> lista = new ArrayList<>();
        for (Recurso recurso : recursos) {
            RecursoProgreso progreso = progresoPorRecurso.get(recurso.getIdRecurso());
            boolean completado = progreso != null && progreso.isCompletado();
            int nivel = progreso != null ? progreso.getNivelDominio() : 0;
            boolean dominado = progreso != null && progreso.isDominado();
            lista.add(new RecursoRutaDTO(recurso, completado, nivel, dominado));
        }

        lista.sort(Comparator
                .comparingInt(this::calcularPrioridad)
                .thenComparingInt(dto -> calcularPrioridad(dto) == 1 ? dto.getNivelDominio() : 0));

        return lista;
    }

    private int calcularPrioridad(RecursoRutaDTO dto) {
        if (dto.isDominado()) return 2;
        if (!dto.isCompletado()) return 0;
        return 1;
    }
}