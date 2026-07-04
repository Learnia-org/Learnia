package com.proyecto.Learnia.service;
import com.proyecto.Learnia.dto.LoginRequest;
import com.proyecto.Learnia.dto.LoginResponse;
import com.proyecto.Learnia.dto.RegisterRequest;
import com.proyecto.Learnia.entity.RolUsuario;
import com.proyecto.Learnia.entity.Usuario;
import com.proyecto.Learnia.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(Usuario usuario){
        if(usuarioRepository.existsByCorreoUsuario(usuario.getCorreoUsuario())){
            throw new RuntimeException("El correo " + usuario.getCorreoUsuario() + " ya esta registrado");
        }
        String hash = passwordEncoder.encode(usuario.getContrasenaUsuario());
        usuario.setContrasenaUsuario(hash);
        usuario.setRolUsuario(RolUsuario.ESTUDIANTE);
        usuario.setFechaRegistro(LocalDateTime.now());
        usuarioRepository.save(usuario);
    }

    public LoginResponse login(LoginRequest req){
        Usuario usuario = usuarioRepository.findByCorreoUsuario(req.correoUsuario)
                .orElseThrow(() -> new RuntimeException("Las credenciales son incorrectas o no existen"));

        boolean ok = passwordEncoder.matches(req.contrasenaUsuario, usuario.getContrasenaUsuario());
        if(!ok) throw new RuntimeException("Credenciales incorrectas: Contraseña incorrecta");
        return new LoginResponse("Bienvenido : ", usuario.getIdUsuario(), usuario.getNombreUsuario(), usuario.getCorreoUsuario()) ;
    }
}