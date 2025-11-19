package com.urbancollection.ecommerce.persistence.jpa.spring;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.urbancollection.ecommerce.domain.entity.ventas.Pedido;

@Repository
public interface PedidoJpaRepository extends JpaRepository<Pedido, Long> {
    
}
