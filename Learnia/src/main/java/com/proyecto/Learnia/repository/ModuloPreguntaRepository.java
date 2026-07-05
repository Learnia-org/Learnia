package com.proyecto.Learnia.repository;

import com.proyecto.Learnia.entity.ModuloPregunta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuloPreguntaRepository extends JpaRepository<ModuloPregunta, Long> {

    List<ModuloPregunta> findByModulo_IdModuloOrderByIdPreguntaModuloAsc(Long idModulo);
}
