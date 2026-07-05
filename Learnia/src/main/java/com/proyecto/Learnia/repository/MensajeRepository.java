package com.proyecto.Learnia.repository;

import com.proyecto.Learnia.entity.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

    @Query("SELECT m FROM Mensaje m " +
           "WHERE (m.emisor.idUsuario = :idA AND m.receptor.idUsuario = :idB) " +
           "   OR (m.emisor.idUsuario = :idB AND m.receptor.idUsuario = :idA) " +
           "ORDER BY m.fechaEnvio ASC")
    List<Mensaje> findConversacion(@Param("idA") Long idA, @Param("idB") Long idB);

    @Query("SELECT m FROM Mensaje m " +
           "WHERE m.emisor.idUsuario = :idUsuario OR m.receptor.idUsuario = :idUsuario " +
           "ORDER BY m.fechaEnvio DESC")
    List<Mensaje> findTodosLosMensajesDe(@Param("idUsuario") Long idUsuario);

    long countByReceptor_IdUsuarioAndEmisor_IdUsuarioAndLeidoFalse(Long idReceptor, Long idEmisor);

    long countByReceptor_IdUsuarioAndLeidoFalse(Long idReceptor);

    @Query("SELECT m FROM Mensaje m " +
           "WHERE m.receptor.idUsuario = :idReceptor AND m.emisor.idUsuario = :idEmisor AND m.leido = false")
    List<Mensaje> findNoLeidos(@Param("idReceptor") Long idReceptor, @Param("idEmisor") Long idEmisor);
}
