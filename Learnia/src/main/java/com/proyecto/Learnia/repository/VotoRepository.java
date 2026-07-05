package com.proyecto.Learnia.repository;


import com.proyecto.Learnia.entity.TipoVoto;
import com.proyecto.Learnia.entity.Voto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VotoRepository extends JpaRepository<Voto, Long> {
    long countByRespuesta_IdRespuestaAndTipoVoto(Long idRespuesta, TipoVoto tipoVoto);

    Optional<Voto> findByUsuario_IdUsuarioAndRespuesta_IdRespuesta(Long idUsuario, Long idRespuesta);

    List<Voto> findAllByUsuario_IdUsuarioAndRespuesta_IdRespuestaOrderByIdVotoDesc(Long idUsuario, Long idRespuesta);

    @Query("SELECT r.usuario.idUsuario, COUNT(v) FROM Voto v JOIN v.respuesta r " +
           "WHERE v.tipoVoto = com.proyecto.Learnia.entity.TipoVoto.LIKE AND r.oculta = false " +
           "GROUP BY r.usuario.idUsuario")
    List<Object[]> countLikesPorUsuario();
}