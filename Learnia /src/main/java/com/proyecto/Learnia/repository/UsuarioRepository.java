package com.proyecto.Learnia.repository;

import com.proyecto.Learnia.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreoUsuario(String correoUsuario);
    boolean existsByCorreoUsuario(String correoUsuario);
    List<Usuario> findByEnLineaTrue();
    List<Usuario> findTop5ByOrderByUltimoAccesoDesc();
    long countByEnLineaTrue();
    long countByBloqueadoFalse();
}