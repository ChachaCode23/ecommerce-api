package com.urbancollection.ecommerce.persistence.jpa.adapters;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.urbancollection.ecommerce.domain.entity.ventas.ItemPedido;
import com.urbancollection.ecommerce.domain.repository.ItemPedidoRepository;
import com.urbancollection.ecommerce.persistence.jpa.spring.ItemPedidoJpaRepository;

@Repository
public class ItemPedidoRepositoryJpaAdapter implements ItemPedidoRepository {

    private final ItemPedidoJpaRepository jpaRepository;

    public ItemPedidoRepositoryJpaAdapter(ItemPedidoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ItemPedido save(ItemPedido itemPedido) {
        return jpaRepository.save(itemPedido);
    }

    @Override
    public ItemPedido findById(Long id) {
        return jpaRepository.findById(id).orElse(null);
    }

    @Override
    public List<ItemPedido> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
