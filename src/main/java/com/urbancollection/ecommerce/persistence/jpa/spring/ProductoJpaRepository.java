package com.urbancollection.ecommerce.persistence.jpa.spring;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.urbancollection.ecommerce.domain.entity.catalogo.Producto;

@Repository
public interface ProductoJpaRepository extends JpaRepository<Producto, Long> {

    // Spring Data genera el query solo con el nombre del método
    Optional<Producto> findByNombreIgnoreCase(String nombre);
}
