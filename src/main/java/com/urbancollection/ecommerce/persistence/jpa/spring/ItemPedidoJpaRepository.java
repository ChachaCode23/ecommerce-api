package com.urbancollection.ecommerce.persistence.jpa.spring;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.urbancollection.ecommerce.domain.entity.ventas.ItemPedido;

@Repository
public interface ItemPedidoJpaRepository extends JpaRepository<ItemPedido, Long> {
    // no extra métodos todavía
}
