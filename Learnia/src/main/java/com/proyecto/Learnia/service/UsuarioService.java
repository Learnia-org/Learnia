package com.proyecto.Learnia.service;

import com.proyecto.Learnia.entity.RolUsuario;
import com.proyecto.Learnia.entity.Usuario;

import java.util.List;

public interface UsuarioService {
    List<Usuario> listar();
    Usuario crear(Usuario usuario);
    Usuario actualizar(Long id, Usuario usuario);
    Usuario buscarPorId(Long id);
    void eliminar(Long id);
    Usuario cambiarRol(Long id, RolUsuario nuevoRol);
    Usuario toggleBloqueo(Long id);
}