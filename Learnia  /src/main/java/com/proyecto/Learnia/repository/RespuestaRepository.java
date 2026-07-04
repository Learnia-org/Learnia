package com.proyecto.Learnia.repository;

import com.proyecto.Learnia.entity.Respuesta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RespuestaRepository extends JpaRepository<Respuesta, Long> {
    List<Respuesta> findByPregunta_IdPregunta(Long idPregunta);
    List<Respuesta> findByUsuario_IdUsuario(Long idUsuario);
    long countByUsuario_IdUsuario(Long idUsuario);

    @Transactional
    void deleteByPregunta_IdPregunta(Long idPregunta);
}