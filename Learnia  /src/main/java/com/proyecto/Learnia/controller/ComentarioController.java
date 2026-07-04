package com.proyecto.Learnia.controller;

import com.proyecto.Learnia.entity.Comentario;
import com.proyecto.Learnia.service.ComentarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/comentarios")
public class ComentarioController {

    private final ComentarioService comentarioService;

    public ComentarioController(ComentarioService comentarioService) {
        this.comentarioService = comentarioService;
    }

    @GetMapping
    public List<Comentario> listar() {
        return comentarioService.listar();
    }

    @PostMapping
    public ResponseEntity<Comentario> crear(@Valid @RequestBody Comentario comentario) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(comentarioService.crear(comentario));
    }

    @GetMapping("/{id}")
    public Comentario buscar(@PathVariable Long id) {
        return comentarioService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public Comentario actualizar(@PathVariable Long id,
                                 @Valid @RequestBody Comentario comentario) {
        return comentarioService.actualizar(id, comentario);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        comentarioService.eliminar(id);
    }
}
