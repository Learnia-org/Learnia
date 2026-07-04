package com.proyecto.Learnia.service;

import com.proyecto.Learnia.dto.PreguntaDTO;
import com.proyecto.Learnia.entity.Pregunta;
import com.proyecto.Learnia.entity.Usuario;
import com.proyecto.Learnia.entity.Categoria;
import com.proyecto.Learnia.exception.ResourceNotFoundException;
import com.proyecto.Learnia.exception.SuccesException;
import com.proyecto.Learnia.repository.PreguntaRepository;
import com.proyecto.Learnia.repository.UsuarioRepository;
import com.proyecto.Learnia.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
        return preguntaRepository.findAll();
    }


    @Override
    public List<Pregunta> buscarPorCategoria(Long idCategoria) {

        return preguntaRepository
                .findByCategoria_IdCategoria(idCategoria);

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
        Pregunta guardada = preguntaRepository.save(pregunta);
        generarRespuestaAutomatica(guardada);
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
            String respuestaIA = geminiService.generarRespuestaParaPregunta(pregunta.getTitulo(), pregunta.getDescripcion());
            respuestaService.guardarComoIA(pregunta.getIdPregunta(), respuestaIA);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
