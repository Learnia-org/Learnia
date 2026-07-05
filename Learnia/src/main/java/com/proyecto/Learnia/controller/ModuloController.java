package com.proyecto.Learnia.controller;

import com.proyecto.Learnia.dto.ModuloResultadoDTO;
import com.proyecto.Learnia.entity.Modulo;
import com.proyecto.Learnia.entity.ModuloPregunta;
import com.proyecto.Learnia.entity.Usuario;
import com.proyecto.Learnia.exception.ResourceNotFoundException;
import com.proyecto.Learnia.repository.UsuarioRepository;
import com.proyecto.Learnia.service.ModuloService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/materias/{curso}/modulos")
public class ModuloController {

    private final ModuloService moduloService;
    private final UsuarioRepository usuarioRepository;

    public ModuloController(ModuloService moduloService, UsuarioRepository usuarioRepository) {
        this.moduloService = moduloService;
        this.usuarioRepository = usuarioRepository;
    }

    private static final Map<String, TemaMateria> TEMAS = new LinkedHashMap<>();

    static {
        TEMAS.put("matematica", new TemaMateria(1L, "Matemática", "fa-solid fa-calculator",
                "bg-indigo-600", "text-indigo-600", "bg-indigo-100", "from-indigo-600 to-indigo-500"));
        TEMAS.put("fisica", new TemaMateria(2L, "Física", "fa-solid fa-atom",
                "bg-blue-600", "text-blue-600", "bg-blue-100", "from-blue-600 to-blue-500"));
        TEMAS.put("informatica", new TemaMateria(3L, "Informática", "fa-solid fa-terminal",
                "bg-cyan-600", "text-cyan-600", "bg-cyan-100", "from-cyan-600 to-cyan-500"));
        TEMAS.put("ingles", new TemaMateria(4L, "Inglés", "fa-solid fa-earth-americas",
                "bg-orange-600", "text-orange-600", "bg-orange-100", "from-orange-600 to-orange-500"));
    }

    private TemaMateria resolverTema(String curso) {
        TemaMateria tema = TEMAS.get(curso);
        if (tema == null) {
            throw new ResourceNotFoundException("La materia '" + curso + "' no existe");
        }
        return tema;
    }

    private Usuario getUsuario(UserDetails userDetails) {
        if (userDetails == null) return null;
        return usuarioRepository.findByCorreoUsuario(userDetails.getUsername()).orElse(null);
    }

    @GetMapping
    public String listarModulos(@PathVariable String curso, Model model,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        TemaMateria tema = resolverTema(curso);
        List<Modulo> modulos = moduloService.listarPorCategoria(tema.getIdCategoria());

        model.addAttribute("curso", curso);
        model.addAttribute("tema", tema);
        model.addAttribute("modulos", modulos);
        model.addAttribute("usuario", getUsuario(userDetails));
        return "cursos/modulos";
    }

    @GetMapping("/{numero}")
    public String verModulo(@PathVariable String curso, @PathVariable Integer numero, Model model,
                             @AuthenticationPrincipal UserDetails userDetails) {
        TemaMateria tema = resolverTema(curso);
        Modulo modulo = moduloService.buscarPorCategoriaYNumero(tema.getIdCategoria(), numero);
        List<ModuloPregunta> preguntas = moduloService.listarPreguntas(modulo.getIdModulo());

        model.addAttribute("curso", curso);
        model.addAttribute("tema", tema);
        model.addAttribute("modulo", modulo);
        model.addAttribute("preguntas", preguntas);
        model.addAttribute("usuario", getUsuario(userDetails));
        return "cursos/modulo-quiz";
    }

    @PostMapping("/{numero}")
    public String enviarRespuestas(@PathVariable String curso, @PathVariable Integer numero,
                                    @RequestParam Map<String, String> parametros, Model model,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        TemaMateria tema = resolverTema(curso);
        Modulo modulo = moduloService.buscarPorCategoriaYNumero(tema.getIdCategoria(), numero);
        List<ModuloPregunta> preguntas = moduloService.listarPreguntas(modulo.getIdModulo());

        Map<Long, String> respuestas = new HashMap<>();
        for (ModuloPregunta pregunta : preguntas) {
            String valor = parametros.get("pregunta_" + pregunta.getIdPreguntaModulo());
            if (valor != null && !valor.isBlank()) {
                respuestas.put(pregunta.getIdPreguntaModulo(), valor);
            }
        }

        ModuloResultadoDTO resultado = moduloService.corregir(modulo.getIdModulo(), respuestas);

        model.addAttribute("curso", curso);
        model.addAttribute("tema", tema);
        model.addAttribute("modulo", modulo);
        model.addAttribute("preguntas", preguntas);
        model.addAttribute("resultado", resultado);
        model.addAttribute("usuario", getUsuario(userDetails));
        return "cursos/modulo-quiz";
    }

    public static class TemaMateria {
        private final Long idCategoria;
        private final String nombre;
        private final String icono;
        private final String colorFondo;
        private final String colorTexto;
        private final String colorSuave;
        private final String gradiente;

        public TemaMateria(Long idCategoria, String nombre, String icono,
                            String colorFondo, String colorTexto, String colorSuave, String gradiente) {
            this.idCategoria = idCategoria;
            this.nombre = nombre;
            this.icono = icono;
            this.colorFondo = colorFondo;
            this.colorTexto = colorTexto;
            this.colorSuave = colorSuave;
            this.gradiente = gradiente;
        }

        public Long getIdCategoria() {
            return idCategoria;
        }

        public String getNombre() {
            return nombre;
        }

        public String getIcono() {
            return icono;
        }

        public String getColorFondo() {
            return colorFondo;
        }

        public String getColorTexto() {
            return colorTexto;
        }

        public String getColorSuave() {
            return colorSuave;
        }

        public String getGradiente() {
            return gradiente;
        }
    }
}
