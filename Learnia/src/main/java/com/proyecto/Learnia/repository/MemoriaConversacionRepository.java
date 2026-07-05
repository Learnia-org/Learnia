package com.proyecto.Learnia.repository;

import com.proyecto.Learnia.entity.MemoriaConversacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemoriaConversacionRepository extends JpaRepository<MemoriaConversacion, Long> {

    List<MemoriaConversacion> findByUsuario_IdUsuarioOrderByFechaAsc(Long idUsuario, Pageable pageable);

    List<MemoriaConversacion> findByUsuario_IdUsuarioOrderByFechaDesc(Long idUsuario, Pageable pageable);

    void deleteByUsuario_IdUsuario(Long idUsuario);

    long countByUsuario_IdUsuario(Long idUsuario);
}
