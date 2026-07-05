package com.proyecto.Learnia.repository;

import com.proyecto.Learnia.entity.Respuesta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RespuestaRepository extends JpaRepository<Respuesta, Long> {
    List<Respuesta> findByPregunta_IdPregunta(Long idPregunta);
    List<Respuesta> findByPregunta_IdPreguntaAndOcultaFalse(Long idPregunta);
    List<Respuesta> findByUsuario_IdUsuario(Long idUsuario);
    long countByUsuario_IdUsuario(Long idUsuario);

    @Transactional
    void deleteByPregunta_IdPregunta(Long idPregunta);

    @Query("SELECT r.usuario.idUsuario, COUNT(r) FROM Respuesta r WHERE r.oculta = false GROUP BY r.usuario.idUsuario")
    List<Object[]> countRespuestasPorUsuario();

    @Query("SELECT r.pregunta.categoria.nombreCategoria, COUNT(r) FROM Respuesta r " +
           "WHERE r.usuario.idUsuario = :idUsuario AND r.oculta = false " +
           "GROUP BY r.pregunta.categoria.nombreCategoria")
    List<Object[]> countRespuestasPorCategoria(@Param("idUsuario") Long idUsuario);
}