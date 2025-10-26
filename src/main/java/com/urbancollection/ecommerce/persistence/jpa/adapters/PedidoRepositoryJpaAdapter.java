package com.urbancollection.ecommerce.persistence.jpa.adapters;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.urbancollection.ecommerce.domain.entity.ventas.Pedido;
import com.urbancollection.ecommerce.domain.repository.PedidoRepository;
import com.urbancollection.ecommerce.persistence.jpa.spring.PedidoJpaRepository;

/**
 * PedidoRepositoryJpaAdapter
 *
 * Adaptador que implementa el repositorio del dominio (PedidoRepository)
 * usando el repositorio Spring Data JPA (PedidoJpaRepository).
 *
 * Así la capa de dominio trabaja con la interfaz PedidoRepository,
 * y esta clase se encarga de hablar con la base de datos.
 */
@Repository
@Transactional
public class PedidoRepositoryJpaAdapter implements PedidoRepository {

    private final PedidoJpaRepository jpa;

    public PedidoRepositoryJpaAdapter(PedidoJpaRepository jpa) {
        this.jpa = jpa;
    }

    /**
     * findById:
     * Busca un pedido por id. Devuelve null si no existe.
     * Marcado como readOnly porque solo consulta.
     */
    @Override
    @Transactional(readOnly = true)
    public Pedido findById(Long id) {
        return jpa.findById(id).orElse(null);
    }

    /**
     * findAll:
     * Devuelve todos los pedidos.
     * También es solo lectura.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Pedido> findAll() {
        return jpa.findAll();
    }

    /**
     * Guarda o actualiza un pedido en BD.
     */
    @Override
    public Pedido save(Pedido pedido) {
        return jpa.save(pedido);
    }

    /**
     * Elimina un pedido por id.
     */
    @Override
    public void delete(Long id) {
        jpa.deleteById(id);
    }
}
