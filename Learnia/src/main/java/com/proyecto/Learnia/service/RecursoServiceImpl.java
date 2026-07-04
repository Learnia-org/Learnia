package com.proyecto.Learnia.service;

import com.proyecto.Learnia.dto.RecursoDTO;
import com.proyecto.Learnia.entity.Categoria;
import com.proyecto.Learnia.entity.Recurso;
import com.proyecto.Learnia.entity.TipoRecurso;
import com.proyecto.Learnia.entity.Usuario;
import com.proyecto.Learnia.exception.ResourceNotFoundException;
import com.proyecto.Learnia.exception.SuccesException;
import com.proyecto.Learnia.repository.CategoriaRepository;
import com.proyecto.Learnia.repository.RecursoRepository;
import com.proyecto.Learnia.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RecursoServiceImpl implements RecursoService{

    private final RecursoRepository recursoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;

    public RecursoServiceImpl(RecursoRepository recursoRepository,
                             UsuarioRepository usuarioRepository,
                             CategoriaRepository categoriaRepository) {
        this.recursoRepository = recursoRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
    }
    @Override
    public List<Recurso> listar() {
        return recursoRepository.findAll();
    }

    @Override
    public Recurso crear(RecursoDTO recursoDTO) {
        Usuario usuario = usuarioRepository.findById(recursoDTO.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Categoria categoria = categoriaRepository.findById(recursoDTO.getIdCategoria())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        Recurso recurso = new Recurso();

        recurso.setTituloRecurso(recursoDTO.getTituloRecurso());
        recurso.setDescripcionRecurso(recursoDTO.getDescripcionRecurso());
        recurso.setTipoRecurso(recursoDTO.getTipoRecurso());
        recurso.setUrlArchivo(recursoDTO.getUrlArchivo());
        recurso.setFechaSubida(LocalDateTime.now());
        recurso.setUsuario(usuario);
        recurso.setCategoria(categoria);
        return recursoRepository.save(recurso);
    }

    @Override
    public Recurso subirArchivo(
            MultipartFile archivo,
            String titulo,
            String descripcion,
            TipoRecurso tipo,
            Long idUsuario,
            Long idCategoria
    ) throws IOException {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada"));

        String carpeta = categoria.getNombreCategoria().toLowerCase();

        Path rutaCarpeta = Paths.get("uploads", carpeta);

        if (!Files.exists(rutaCarpeta)) {
            Files.createDirectories(rutaCarpeta);
        }

        String nombreArchivo = UUID.randomUUID()
                + "_" + archivo.getOriginalFilename();

        Path rutaArchivo = rutaCarpeta.resolve(nombreArchivo);

        Files.copy(
                archivo.getInputStream(),
                rutaArchivo,
                StandardCopyOption.REPLACE_EXISTING
        );

        Recurso recurso = new Recurso();

        recurso.setTituloRecurso(titulo);
        recurso.setDescripcionRecurso(descripcion);
        recurso.setTipoRecurso(tipo);

        recurso.setUrlArchivo("/uploads/" + carpeta + "/" + nombreArchivo);

        recurso.setFechaSubida(LocalDateTime.now());

        recurso.setUsuario(usuario);
        recurso.setCategoria(categoria);

        return recursoRepository.save(recurso);
    }

    @Override
    public Recurso buscarPorIdRec(Long id) {
        return recursoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Recurso no encontrado por id: " + id));
    }

    @Override
    public Recurso actualizarReC(Long id, RecursoDTO dto) {

        Recurso existe = buscarPorIdRec(id);

        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Categoria categoria = categoriaRepository.findById(dto.getIdCategoria())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        existe.setTituloRecurso(dto.getTituloRecurso());
        existe.setDescripcionRecurso(dto.getDescripcionRecurso());
        existe.setTipoRecurso(dto.getTipoRecurso());
        existe.setUrlArchivo(dto.getUrlArchivo());
        existe.setUsuario(usuario);
        existe.setCategoria(categoria);

        return recursoRepository.save(existe);
    }

    @Override
    public void eliminar(Long id) {

        Recurso recurso = buscarPorIdRec(id);

        try {
            Path rutaArchivo = Paths.get(recurso.getUrlArchivo().substring(1));
            Files.deleteIfExists(rutaArchivo);
        } catch (IOException e) {
            throw new RuntimeException("Error eliminando archivo");
        }

        recursoRepository.delete(recurso);
    }

}
