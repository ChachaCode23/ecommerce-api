package com.urbancollection.ecommerce.persistence.jpa.adapters;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.urbancollection.ecommerce.domain.entity.ventas.Pedido;
import com.urbancollection.ecommerce.domain.repository.PedidoRepository;
import com.urbancollection.ecommerce.persistence.jpa.spring.PedidoJpaRepository;

@Repository
@Transactional
public class PedidoRepositoryJpaAdapter implements PedidoRepository {

    private final PedidoJpaRepository jpa;

    public PedidoRepositoryJpaAdapter(PedidoJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    @Transactional(readOnly = true)
    public Pedido findById(Long id) {
        return jpa.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> findAll() {
        return jpa.findAll();
    }

    @Override
    public Pedido save(Pedido pedido) {
        return jpa.save(pedido);
    }

    @Override
    public void delete(Long id) {
        jpa.deleteById(id);
    }
}
