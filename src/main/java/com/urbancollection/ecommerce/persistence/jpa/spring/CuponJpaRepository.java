package com.urbancollection.ecommerce.persistence.jpa.spring;

import com.urbancollection.ecommerce.domain.entity.catalogo.Cupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CuponJpaRepository extends JpaRepository<Cupon, Long> {
    
    // ✅ Método para buscar cupón por código
    Optional<Cupon> findByCodigo(String codigo);
}