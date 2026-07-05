package com.proyecto.Learnia.repository;

import com.proyecto.Learnia.entity.Modulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuloRepository extends JpaRepository<Modulo, Long> {

    List<Modulo> findByCategoria_IdCategoriaOrderByNumeroAsc(Long idCategoria);

    Optional<Modulo> findByCategoria_IdCategoriaAndNumero(Long idCategoria, Integer numero);

    long countByCategoria_IdCategoria(Long idCategoria);
}
