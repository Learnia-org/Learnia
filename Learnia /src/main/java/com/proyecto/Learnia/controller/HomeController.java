package com.proyecto.Learnia.controller;

import com.proyecto.Learnia.dto.PreguntaDTO;
import com.proyecto.Learnia.entity.Categoria;
import com.proyecto.Learnia.entity.Pregunta;
import com.proyecto.Learnia.entity.Respuesta;
import com.proyecto.Learnia.entity.Usuario;
import com.proyecto.Learnia.repository.CategoriaRepository;
import com.proyecto.Learnia.repository.PreguntaRepository;
import com.proyecto.Learnia.repository.RespuestaRepository;
import com.proyecto.Learnia.repository.UsuarioRepository;
import com.proyecto.Learnia.service.PreguntaService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Controller
public class HomeController {

    private final UsuarioRepository usuarioRepository;
    private final PreguntaService preguntaService;
    private final CategoriaRepository categoriaRepository;
    private final PreguntaRepository preguntaRepository;
    private final RespuestaRepository respuestaRepository;
    private final PasswordEncoder passwordEncoder;

    public HomeController(UsuarioRepository usuarioRepository,
                          PreguntaService preguntaService,
                          CategoriaRepository categoriaRepository,
                          PreguntaRepository preguntaRepository,
                          RespuestaRepository respuestaRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.preguntaService = preguntaService;
        this.categoriaRepository = categoriaRepository;
        this.preguntaRepository = preguntaRepository;
        this.respuestaRepository = respuestaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Pregunta> preguntas = preguntaService.listar();
        model.addAttribute("preguntas", preguntas);
        return "home";
    }

    @GetMapping("/menu")
    public String menu(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository
                .findByCorreoUsuario(userDetails.getUsername())
                .orElseThrow();

        List<Pregunta> preguntas = preguntaService.listar();
        List<Categoria> categorias = categoriaRepository.findByActivaTrue();

        model.addAttribute("usuario", usuario);
        model.addAttribute("preguntas", preguntas);
        model.addAttribute("categorias", categorias);

        return "menu-usuario";
    }

    @GetMapping("/usuario")
    public String vistaUsuario(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository
                .findByCorreoUsuario(userDetails.getUsername())
                .orElseThrow();

        long totalDudas = preguntaRepository.findByUsuario_IdUsuario(usuario.getIdUsuario()).size();
        long totalAportes = respuestaRepository.countByUsuario_IdUsuario(usuario.getIdUsuario());

        model.addAttribute("usuario", usuario);
        model.addAttribute("totalDudas", totalDudas);
        model.addAttribute("totalAportes", totalAportes);
        return "usuarios-view";
    }

    @PostMapping("/usuario/actualizar")
    public String actualizarUsuario(@AuthenticationPrincipal UserDetails userDetails,
                                    @RequestParam("nombreUsuario") String nombreUsuario,
                                    @RequestParam(value = "nuevaContrasena", required = false) String nuevaContrasena,
                                    @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
                                    Model model) {
        Usuario usuario = usuarioRepository
                .findByCorreoUsuario(userDetails.getUsername())
                .orElseThrow();

        usuario.setNombreUsuario(nombreUsuario);

        if (nuevaContrasena != null && !nuevaContrasena.isBlank()) {
            usuario.setContrasenaUsuario(passwordEncoder.encode(nuevaContrasena));
        }

        if (imagenFile != null && !imagenFile.isEmpty()) {
            try {
                String uploadDir = "uploads/perfiles/";
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                String filename = UUID.randomUUID() + "_" + imagenFile.getOriginalFilename();
                Path path = Paths.get(uploadDir + filename);
                Files.write(path, imagenFile.getBytes());
                usuario.setFotoUsuario("/" + uploadDir + filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        usuarioRepository.save(usuario);
        return "redirect:/usuario?guardado";
    }

    @GetMapping("/mis-preguntas")
    public String misPreguntas(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository
                .findByCorreoUsuario(userDetails.getUsername())
                .orElseThrow();

        List<Pregunta> misPreguntas = preguntaRepository.findByUsuario_IdUsuario(usuario.getIdUsuario());
        List<Respuesta> misRespuestas = respuestaRepository.findByUsuario_IdUsuario(usuario.getIdUsuario());

        model.addAttribute("usuario", usuario);
        model.addAttribute("misPreguntas", misPreguntas);
        model.addAttribute("misRespuestas", misRespuestas);
        model.addAttribute("totalDudas", misPreguntas.size());
        model.addAttribute("totalAportes", misRespuestas.size());

        return "mis-preguntas";
    }

    @GetMapping("/api/mi-impacto")
    @ResponseBody
    public java.util.Map<String, Long> miImpacto(@AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository
                .findByCorreoUsuario(userDetails.getUsername())
                .orElseThrow();

        long dudas = preguntaRepository.findByUsuario_IdUsuario(usuario.getIdUsuario()).size();
        long aportes = respuestaRepository.countByUsuario_IdUsuario(usuario.getIdUsuario());

        return java.util.Map.of("dudas", dudas, "aportes", aportes);
    }

    @GetMapping("/preguntar")
    public String preguntar(Model model) {
        model.addAttribute("preguntaDTO", new PreguntaDTO());
        List<Categoria> categorias = categoriaRepository.findByActivaTrue();
        model.addAttribute("categorias", categorias);
        return "preguntar";
    }

    @PostMapping("/preguntar")
    public String guardarPregunta(@ModelAttribute PreguntaDTO dto,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository
                .findByCorreoUsuario(userDetails.getUsername())
                .orElseThrow();
        dto.setIdUsuario(usuario.getIdUsuario());
        preguntaService.guardar(dto);
        return "redirect:/menu";
    }

    @GetMapping("/menu-admin")
    public String menuAdmin(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository
                .findByCorreoUsuario(userDetails.getUsername())
                .orElseThrow();

        long totalUsuarios = usuarioRepository.count();
        long enLinea = usuarioRepository.countByEnLineaTrue();
        long preguntasHoy = preguntaRepository.countByFechaPublicacionAfter(
                LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT));
        long categoriasActivas = categoriaRepository.countByActivaTrue();

        List<Usuario> usuariosRecientes = usuarioRepository.findTop5ByOrderByUltimoAccesoDesc();
        List<Categoria> categorias = categoriaRepository.findAll();

        model.addAttribute("usuario", usuario);
        model.addAttribute("totalUsuarios", totalUsuarios);
        model.addAttribute("enLinea", enLinea);
        model.addAttribute("preguntasHoy", preguntasHoy);
        model.addAttribute("categoriasActivas", categoriasActivas);
        model.addAttribute("usuariosRecientes", usuariosRecientes);
        model.addAttribute("categorias", categorias);

        return "menu-admin";
    }

    @GetMapping("/admin/usuarios")
    public String adminUsuarios(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Usuario admin = usuarioRepository.findByCorreoUsuario(userDetails.getUsername()).orElseThrow();
        List<Usuario> todos = usuarioRepository.findAll();
        List<Usuario> enLinea = usuarioRepository.findByEnLineaTrue();

        model.addAttribute("usuario", admin);
        model.addAttribute("todosUsuarios", todos);
        model.addAttribute("enLinea", enLinea);
        model.addAttribute("totalUsuarios", todos.size());
        model.addAttribute("totalEnLinea", enLinea.size());
        model.addAttribute("totalBloqueados", todos.stream().filter(u -> u.isBloqueado()).count());

        return "admin-usuarios";
    }

    @PostMapping("/admin/usuarios/{id}/bloquear")
    public String bloquearUsuario(@PathVariable Long id) {
        usuarioRepository.findById(id).ifPresent(u -> {
            u.setBloqueado(!u.isBloqueado());
            u.setEnLinea(false);
            usuarioRepository.save(u);
        });
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/admin/usuarios/{id}/eliminar")
    public String eliminarUsuario(@PathVariable Long id) {
        try {
            usuarioRepository.deleteById(id);
        } catch (Exception e) {
            return "redirect:/admin/usuarios?errorEliminar";
        }
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/admin/usuarios/{id}/cambiarRol")
    public String cambiarRol(@PathVariable Long id, @RequestParam String nuevoRol) {
        usuarioRepository.findById(id).ifPresent(u -> {
            try {
                u.setRolUsuario(com.proyecto.Learnia.entity.RolUsuario.valueOf(nuevoRol.toUpperCase()));
                usuarioRepository.save(u);
            } catch (IllegalArgumentException ignored) {
            }
        });
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/admin/preguntas")
    public String adminPreguntas(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Usuario admin = usuarioRepository.findByCorreoUsuario(userDetails.getUsername()).orElseThrow();
        List<Pregunta> preguntas = preguntaRepository.findAll();
        List<Categoria> categorias = categoriaRepository.findAll();

        long totalRespuestas = respuestaRepository.count();
        long totalOcultas = preguntas.stream().filter(p -> p.isOculta()).count();

        model.addAttribute("usuario", admin);
        model.addAttribute("preguntas", preguntas);
        model.addAttribute("categorias", categorias);
        model.addAttribute("totalPreguntas", preguntas.size());
        model.addAttribute("totalRespuestas", totalRespuestas);
        model.addAttribute("totalOcultas", totalOcultas);

        return "admin-preguntas";
    }

    @PostMapping("/admin/preguntas/{id}/ocultar")
    public String ocultarPregunta(@PathVariable Long id) {
        preguntaRepository.findById(id).ifPresent(p -> {
            p.setOculta(!p.isOculta());
            preguntaRepository.save(p);
        });
        return "redirect:/admin/preguntas";
    }

    @PostMapping("/admin/preguntas/{id}/eliminar")
    public String eliminarPregunta(@PathVariable Long id) {
        try {
            respuestaRepository.deleteByPregunta_IdPregunta(id);
            preguntaRepository.deleteById(id);
        } catch (Exception e) {
            return "redirect:/admin/preguntas?errorEliminar";
        }
        return "redirect:/admin/preguntas";
    }

    @PostMapping("/admin/respuestas/{id}/eliminar")
    public String eliminarRespuesta(@PathVariable Long id) {
        try {
            respuestaRepository.deleteById(id);
        } catch (Exception e) {
            return "redirect:/admin/preguntas?errorEliminar";
        }
        return "redirect:/admin/preguntas";
    }

    @GetMapping("/admin/categorias")
    public String adminCategorias(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Usuario admin = usuarioRepository.findByCorreoUsuario(userDetails.getUsername()).orElseThrow();
        List<Categoria> activas = categoriaRepository.findByActivaTrue();
        List<Categoria> inactivas = categoriaRepository.findByActivaFalse();
        List<Categoria> todas = categoriaRepository.findAll();

        model.addAttribute("usuario", admin);
        model.addAttribute("activas", activas);
        model.addAttribute("inactivas", inactivas);
        model.addAttribute("todas", todas);

        return "admin-categorias";
    }

    @PostMapping("/admin/categorias/crear")
    public String crearCategoria(@RequestParam String nombre,
                                 @RequestParam String descripcion) {
        Categoria cat = new Categoria();
        cat.setNombreCategoria(nombre);
        cat.setDescripcionCategoria(descripcion);
        cat.setActiva(true);
        categoriaRepository.save(cat);
        return "redirect:/admin/categorias";
    }

    @PostMapping("/admin/categorias/{id}/toggleActiva")
    public String toggleCategoria(@PathVariable Long id) {
        categoriaRepository.findById(id).ifPresent(c -> {
            c.setActiva(!c.isActiva());
            categoriaRepository.save(c);
        });
        return "redirect:/admin/categorias";
    }

    @PostMapping("/admin/categorias/{id}/eliminar")
    public String eliminarCategoria(@PathVariable Long id) {
        categoriaRepository.deleteById(id);
        return "redirect:/admin/categorias";
    }

    @PostMapping("/admin/categorias/{id}/editar")
    public String editarCategoria(@PathVariable Long id,
                                  @RequestParam String nombre,
                                  @RequestParam String descripcion) {
        categoriaRepository.findById(id).ifPresent(c -> {
            c.setNombreCategoria(nombre);
            c.setDescripcionCategoria(descripcion);
            categoriaRepository.save(c);
        });
        return "redirect:/admin/categorias";
    }

    @GetMapping("/api/admin/stats")
    @ResponseBody
    public java.util.Map<String, Long> adminStats() {
        long totalUsuarios = usuarioRepository.count();
        long enLinea = usuarioRepository.countByEnLineaTrue();
        long preguntasHoy = preguntaRepository.countByFechaPublicacionAfter(
                LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT));
        long categoriasActivas = categoriaRepository.countByActivaTrue();

        return java.util.Map.of(
                "totalUsuarios", totalUsuarios,
                "enLinea", enLinea,
                "preguntasHoy", preguntasHoy,
                "categoriasActivas", categoriasActivas
        );
    }
}