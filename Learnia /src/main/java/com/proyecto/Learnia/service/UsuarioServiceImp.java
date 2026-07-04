package com.proyecto.Learnia.service;
import com.proyecto.Learnia.entity.RolUsuario;
import com.proyecto.Learnia.entity.Usuario;
import com.proyecto.Learnia.exception.ResourceNotFoundException;
import com.proyecto.Learnia.exception.SuccesException;
import com.proyecto.Learnia.repository.ComentarioRepository;
import com.proyecto.Learnia.repository.PreguntaRepository;
import com.proyecto.Learnia.repository.RecursoRepository;
import com.proyecto.Learnia.repository.UsuarioRepository;
import com.proyecto.Learnia.service.UsuarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioServiceImp implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImp(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario crear(Usuario usuario) {
        if (usuarioRepository.existsByCorreoUsuario(usuario.getCorreoUsuario())) {
            throw new RuntimeException("El correo " + usuario.getCorreoUsuario() + " ya está registrado");
        }
        usuario.setIdUsuario(null);
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario actualizar(Long id, Usuario usuario) {
        Usuario existente = buscarPorId(id);
        existente.setNombreUsuario(usuario.getNombreUsuario());
        existente.setContrasenaUsuario(usuario.getContrasenaUsuario());
        existente.setCorreoUsuario(usuario.getCorreoUsuario());
        existente.setFechaRegistro(usuario.getFechaRegistro());
        existente.setRolUsuario(usuario.getRolUsuario());
        existente.setFotoUsuario(usuario.getFotoUsuario());
        return usuarioRepository.save(existente);
    }

    @Override
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con id no encontrado: " + id));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario con id no existente o no encontrado: " + id);
        }
        usuarioRepository.deleteById(id);
        throw new SuccesException("Usuario eliminado correctamente");
    }

    @Override
    @Transactional
    public Usuario cambiarRol(Long id, RolUsuario nuevoRol) {
        Usuario usuario = buscarPorId(id);
        usuario.setRolUsuario(nuevoRol);
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public Usuario toggleBloqueo(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setBloqueado(!usuario.isBloqueado());
        if (usuario.isBloqueado()) {
            usuario.setEnLinea(false);
        }
        return usuarioRepository.save(usuario);
    }
}