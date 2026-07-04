package com.proyecto.Learnia.repository;


import com.proyecto.Learnia.entity.TipoVoto;
import com.proyecto.Learnia.entity.Voto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VotoRepository extends JpaRepository<Voto, Long> {
    long countByRespuesta_IdRespuestaAndTipoVoto(Long idRespuesta, TipoVoto tipoVoto);

    Optional<Voto> findByUsuario_IdUsuarioAndRespuesta_IdRespuesta(Long idUsuario, Long idRespuesta);

    List<Voto> findAllByUsuario_IdUsuarioAndRespuesta_IdRespuestaOrderByIdVotoDesc(Long idUsuario, Long idRespuesta);
}