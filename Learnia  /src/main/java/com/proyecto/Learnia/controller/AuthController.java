package com.proyecto.Learnia.controller;
import com.proyecto.Learnia.dto.LoginRequest;
import com.proyecto.Learnia.dto.LoginResponse;
import com.proyecto.Learnia.dto.RegisterRequest;
import com.proyecto.Learnia.entity.Usuario;
import com.proyecto.Learnia.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/register")
    public String mostrarRegister(Model model){
        model.addAttribute("user", new Usuario());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") Usuario usuario,
                           BindingResult result,
                           Model model){
        if(result.hasErrors()){
            return "register";
        }
        try{
            authService.register(usuario);
            return "redirect:/login?success";
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
