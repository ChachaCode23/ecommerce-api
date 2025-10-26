package com.urbancollection.ecommerce.persistence.jpa.spring;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.urbancollection.ecommerce.domain.entity.logistica.Direccion;

@Repository
public interface DireccionJpaRepository extends JpaRepository<Direccion, Long> {
    // no extra métodos por ahora
}
