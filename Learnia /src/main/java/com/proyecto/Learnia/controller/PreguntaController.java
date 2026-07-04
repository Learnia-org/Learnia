package com.proyecto.Learnia.controller;

import com.proyecto.Learnia.dto.PreguntaDTO;
import com.proyecto.Learnia.entity.Pregunta;
import com.proyecto.Learnia.service.PreguntaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/preguntas")
public class PreguntaController {

    private final PreguntaService preguntaService;

    public PreguntaController(PreguntaService preguntaService) {
        this.preguntaService = preguntaService;
    }

    // LISTAR TODO: GET http://localhost:8080/api/preguntas
    @GetMapping
    public List<Pregunta> listar() {
        return preguntaService.listar();
    }

    // GUARDAR: POST http://localhost:8080/api/preguntas
    @PostMapping
    public ResponseEntity<Pregunta> guardar(@Valid @RequestBody PreguntaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(preguntaService.guardar(dto));
    }

    // BUSCAR: GET http://localhost:8080/api/preguntas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Pregunta> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(preguntaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pregunta> actualizar(@PathVariable Long id, @Valid @RequestBody PreguntaDTO dto) {
        return ResponseEntity.ok(preguntaService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        preguntaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}