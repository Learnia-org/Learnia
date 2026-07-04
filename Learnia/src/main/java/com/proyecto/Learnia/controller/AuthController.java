package com.proyecto.Learnia.controller;
import com.proyecto.Learnia.dto.LoginRequest;
import com.proyecto.Learnia.dto.LoginResponse;
import com.proyecto.Learnia.dto.RegisterRequest;
import com.proyecto.Learnia.entity.Usuario;
import com.proyecto.Learnia.service.AuthService;
import com.proyecto.Learnia.service.FileStorageService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class AuthController {
    private final AuthService authService;
    private final FileStorageService fileStorageService;

    public AuthController(AuthService authService, FileStorageService fileStorageService) {
        this.authService = authService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/register")
    public String mostrarRegister(Model model){
        model.addAttribute("user", new Usuario());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") Usuario usuario,
                           BindingResult result,
                           @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
                           Model model){
        if(result.hasErrors()){
            return "register";
        }
        try{
            if (imagenFile != null && !imagenFile.isEmpty()) {
                usuario.setFotoUsuario(fileStorageService.guardarFotoPerfil(imagenFile));
            }
            authService.register(usuario);
            return "redirect:/login?success";
        }catch (IOException e){
            model.addAttribute("error", "No se pudo guardar la foto de perfil. Intenta de nuevo.");
            return "register";
        }catch (RuntimeException e){
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }
}
