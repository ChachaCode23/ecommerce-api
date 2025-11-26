package com.urbancollection.ecommerce.persistence.jpa.spring;

import com.urbancollection.ecommerce.domain.entity.logistica.Envio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnvioJpaRepository extends JpaRepository<Envio, Long> {
    
    // ✅ Método para buscar envío por pedido
    Optional<Envio> findByPedidoId(Long pedidoId);
    
    // ✅ Método para buscar envío por tracking
    Optional<Envio> findByTracking(String tracking);
}