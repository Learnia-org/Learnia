package com.proyecto.Learnia.controller;

import com.proyecto.Learnia.dto.RecursoDTO;
import com.proyecto.Learnia.entity.Recurso;
import com.proyecto.Learnia.entity.TipoRecurso;
import com.proyecto.Learnia.entity.Usuario;
import com.proyecto.Learnia.repository.UsuarioRepository;
import com.proyecto.Learnia.service.RecursoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/recurso")
public class RecursoController {

    private final RecursoService recursoService;
    private final UsuarioRepository usuarioRepository;

    public RecursoController(RecursoService recursoService, UsuarioRepository usuarioRepository) {
        this.recursoService = recursoService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    @ResponseBody
    public List<Recurso> listar() {
        return recursoService.listar();
    }

    @PostMapping
    public ResponseEntity<Recurso> crear(@Valid @RequestBody RecursoDTO recursoDTO) {
        Recurso crear = recursoService.crear(recursoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(crear);
    }

    @PostMapping("/subir")
    public String subirArhivo(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("titulo") String tituloRecurso,
            @RequestParam("descripcionRecurso") String descripcionRecurso,
            @RequestParam("tipoRecurso") TipoRecurso tipoRecurso,
            @RequestParam("idCategoria") Long idCategoria,
            @AuthenticationPrincipal UserDetails userDetails
    ) throws IOException {

        Usuario usuario = usuarioRepository.findByCorreoUsuario(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        recursoService.subirArchivo(
                archivo,
                tituloRecurso,
                descripcionRecurso,
                tipoRecurso,
                usuario.getIdUsuario(),
                idCategoria
        );

        if (idCategoria == 1) return "redirect:/materias/matematica";
        if (idCategoria == 2) return "redirect:/materias/fisica";
        if (idCategoria == 3) return "redirect:/materias/informatica";
        if (idCategoria == 4) return "redirect:/materias/ingles";

        return "redirect:/inicio";
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Recurso buscar(@PathVariable Integer id) {
        return recursoService.buscarPorIdRec(id.longValue());
    }

    @PutMapping("/{id}")
    @ResponseBody
    public Recurso actualizar(@PathVariable Integer id, @Valid @RequestBody RecursoDTO dto) {
        return recursoService.actualizarReC(id.longValue(), dto);
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarWeb(@PathVariable Long id) {
        Recurso recurso = recursoService.buscarPorIdRec(id);
        Long categoriaId = recurso.getCategoria().getIdCategoria();
        recursoService.eliminar(id);

        if (categoriaId == 1) return "redirect:/materias/matematica";
        if (categoriaId == 2) return "redirect:/materias/fisica";
        if (categoriaId == 3) return "redirect:/materias/informatica";
        if (categoriaId == 4) return "redirect:/materias/ingles";

        return "redirect:/inicio";
    }
}