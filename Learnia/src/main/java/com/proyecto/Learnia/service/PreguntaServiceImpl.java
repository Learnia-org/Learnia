package com.proyecto.Learnia.service;

import com.proyecto.Learnia.dto.PreguntaDTO;
import com.proyecto.Learnia.dto.PreguntaSimilarDTO;
import com.proyecto.Learnia.entity.Pregunta;
import com.proyecto.Learnia.entity.Usuario;
import com.proyecto.Learnia.entity.Categoria;
import com.proyecto.Learnia.exception.ResourceNotFoundException;
import com.proyecto.Learnia.exception.SuccesException;
import com.proyecto.Learnia.repository.PreguntaRepository;
import com.proyecto.Learnia.repository.UsuarioRepository;
import com.proyecto.Learnia.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PreguntaServiceImpl implements PreguntaService {

    private final PreguntaRepository preguntaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final GeminiService geminiService;
    private final RespuestaService respuestaService;

    public PreguntaServiceImpl(PreguntaRepository preguntaRepository,
                               UsuarioRepository usuarioRepository,
                               CategoriaRepository categoriaRepository,
                               GeminiService geminiService,
                               RespuestaService respuestaService) {
        this.preguntaRepository = preguntaRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
        this.geminiService = geminiService;
        this.respuestaService = respuestaService;
    }

    @Override
    public List<Pregunta> listar() {
        return preguntaRepository.findByOcultaFalse();
    }


    @Override
    public List<Pregunta> buscarPorCategoria(Long idCategoria) {

        return preguntaRepository
                .findByCategoria_IdCategoriaAndOcultaFalse(idCategoria);

    }

    @Override
    public Pregunta buscarPorId(Long id) {
        return preguntaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pregunta no encontrada con ID: " + id));
    }

    @Override
    public Pregunta guardar(PreguntaDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + dto.getIdUsuario()));
        Categoria categoria = categoriaRepository.findById(dto.getIdCategoria())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + dto.getIdCategoria()));
        Pregunta pregunta = new Pregunta();
        pregunta.setTitulo(dto.getTitulo());
        pregunta.setDescripcion(dto.getDescripcion());
        pregunta.setUsuario(usuario);
        pregunta.setCategoria(categoria);
        pregunta.setFechaPublicacion(LocalDateTime.now());
        pregunta.setImagenUrl(dto.getImagenUrl());

        String contenidoCompleto = dto.getTitulo() + ". " + dto.getDescripcion();
        boolean contieneProhibido = geminiService.contieneContenidoProhibido(contenidoCompleto);
        boolean tieneSentido = !contieneProhibido && geminiService.esContenidoCoherente(contenidoCompleto);
        boolean valida = false;
        String respuestaGenerada = null;

        if (tieneSentido) {
            GeminiService.ResultadoPregunta resultado = geminiService.evaluarPregunta(
                    dto.getTitulo(), dto.getDescripcion(), categoria.getNombreCategoria());
            valida = resultado.valida();
            respuestaGenerada = resultado.respuesta();
        }

        pregunta.setOculta(!valida);
        Pregunta guardada = preguntaRepository.save(pregunta);

        if (valida) {
            if (respuestaGenerada != null && !respuestaGenerada.isBlank()) {
                try {
                    respuestaService.guardarComoIA(guardada.getIdPregunta(), respuestaGenerada);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                generarRespuestaAutomatica(guardada);
            }
        }

        return guardada;
    }

    @Override
    public Pregunta actualizar(Long id, PreguntaDTO dto) {
        Pregunta existente = buscarPorId(id);
        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + dto.getIdUsuario()));
        Categoria categoria = categoriaRepository.findById(dto.getIdCategoria())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + dto.getIdCategoria()));
        existente.setTitulo(dto.getTitulo());
        existente.setDescripcion(dto.getDescripcion());
        existente.setUsuario(usuario);
        existente.setCategoria(categoria);
        return preguntaRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        if (!preguntaRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar, ID no encontrado: " + id);
        }
        preguntaRepository.deleteById(id);
        throw new SuccesException("Pregunta eliminada correctamente");
    }

    private void generarRespuestaAutomatica(Pregunta pregunta) {
        try {
            GeminiService.ResultadoPregunta resultado = geminiService.evaluarPregunta(
                    pregunta.getTitulo(), pregunta.getDescripcion(),
                    pregunta.getCategoria() != null ? pregunta.getCategoria().getNombreCategoria() : "");
            if (resultado.respuesta() != null && !resultado.respuesta().isBlank()) {
                respuestaService.guardarComoIA(pregunta.getIdPregunta(), resultado.respuesta());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<PreguntaSimilarDTO> buscarSimilares(String texto, Long idCategoria) {
        Set<String> palabrasTexto = tokenizar(texto);
        if (palabrasTexto.isEmpty()) {
            return List.of();
        }

        List<Pregunta> candidatas = idCategoria != null
                ? preguntaRepository.findByCategoria_IdCategoriaAndOcultaFalse(idCategoria)
                : preguntaRepository.findByOcultaFalse();

        return candidatas.stream()
                .map(p -> new Object[]{p, palabrasTexto, tokenizar(p.getTitulo() + " " + p.getDescripcion())})
                .filter(par -> coincidenciaSuficiente((Set<String>) par[1], (Set<String>) par[2]))
                .map(par -> new Object[]{par[0], similitudJaccard((Set<String>) par[1], (Set<String>) par[2])})
                .filter(par -> ((double) par[1]) >= 0.3)
                .sorted((a, b) -> Double.compare((double) b[1], (double) a[1]))
                .limit(4)
                .map(par -> {
                    Pregunta p = (Pregunta) par[0];
                    String categoria = p.getCategoria() != null ? p.getCategoria().getNombreCategoria() : "General";
                    return new PreguntaSimilarDTO(p.getIdPregunta(), p.getTitulo(), categoria);
                })
                .toList();
    }

    private boolean coincidenciaSuficiente(Set<String> a, Set<String> b) {
        Set<String> interseccion = new HashSet<>(a);
        interseccion.retainAll(b);
        if (interseccion.isEmpty()) {
            return false;
        }
        if (interseccion.size() >= 2) {
            return true;
        }
        return interseccion.stream().anyMatch(palabra -> palabra.length() >= 6);
    }

    private static final Set<String> STOPWORDS = Set.of(
            "de", "la", "el", "en", "y", "a", "que", "los", "las", "un", "una", "con",
            "para", "del", "es", "como", "por", "se", "su", "al", "lo", "mi", "tu", "cual",
            "cuales", "porque", "cuando", "donde", "sobre", "entre", "esto", "esta", "este",
            "quiero", "quisiera", "quisieramos", "aprender", "saber", "entender", "explicar",
            "explicarme", "ayuda", "ayudenme", "necesito", "necesitamos", "gustaria", "puedo",
            "podria", "podrian", "podrias", "hacer", "tener", "tengo", "tienen", "hay", "muy",
            "bien", "bueno", "buena", "gusta", "cosas", "cosa", "algo", "nada", "todo", "toda",
            "todos", "todas", "mas", "menos", "ese", "esa", "esos", "esas", "soy", "estoy",
            "sera", "seria", "pueden", "alguien", "algun", "alguna", "dudas", "duda", "pregunta",
            "tema", "temas", "manera", "forma"
    );

    private Set<String> tokenizar(String texto) {
        if (texto == null || texto.isBlank()) {
            return Set.of();
        }
        String limpio = Normalizer.normalize(texto.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^a-z0-9\\s]", " ");

        Set<String> resultado = new HashSet<>();
        for (String palabra : limpio.split("\\s+")) {
            if (palabra.length() >= 3 && !STOPWORDS.contains(palabra)) {
                resultado.add(palabra);
            }
        }
        return resultado;
    }

    private double similitudJaccard(Set<String> a, Set<String> b) {
        if (a.isEmpty() || b.isEmpty()) {
            return 0.0;
        }
        Set<String> interseccion = new HashSet<>(a);
        interseccion.retainAll(b);
        Set<String> union = new HashSet<>(a);
        union.addAll(b);
        return (double) interseccion.size() / union.size();
    }

    private Pregunta mapearYGuardar(Pregunta entidad, PreguntaDTO dto) {
        entidad.setTitulo(dto.getTitulo());
        entidad.setDescripcion(dto.getDescripcion());

        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Categoria categoria = categoriaRepository.findById(dto.getIdCategoria())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        entidad.setUsuario(usuario);
        entidad.setCategoria(categoria);

        return preguntaRepository.save(entidad);
    }
}
