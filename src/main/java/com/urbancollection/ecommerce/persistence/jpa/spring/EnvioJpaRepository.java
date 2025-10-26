package com.urbancollection.ecommerce.persistence.jpa.spring;

import com.urbancollection.ecommerce.domain.entity.logistica.Envio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnvioJpaRepository extends JpaRepository<Envio, Long> {
    // no custom methods todavía
}
