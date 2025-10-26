package com.urbancollection.ecommerce.persistence.jpa.spring;

import com.urbancollection.ecommerce.domain.entity.catalogo.Cupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CuponJpaRepository extends JpaRepository<Cupon, Long> {
    // no extra métodos por ahora
}
