package com.urbancollection.ecommerce.persistence.jpa.adapters;

import com.urbancollection.ecommerce.domain.entity.ventas.ListaDeseos;
import com.urbancollection.ecommerce.domain.repository.ListaDeseosRepository;
import com.urbancollection.ecommerce.persistence.jpa.spring.ListaDeseosJpaRepository;

import java.util.List;

public class ListaDeseosRepositoryJpaAdapter implements ListaDeseosRepository {

    private final ListaDeseosJpaRepository jpa;

    public ListaDeseosRepositoryJpaAdapter(ListaDeseosJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public ListaDeseos findById(Long id) {
        return jpa.findById(id).orElse(null);
    }

    @Override
    public List<ListaDeseos> findAll() {
        return jpa.findAll();
    }

    @Override
    public ListaDeseos save(ListaDeseos entity) {
        return jpa.save(entity);
    }

    @Override
    public void delete(Long id) {
        jpa.deleteById(id);
    }

    @Override
    public boolean existsByUsuarioIdAndProductoId(Long usuarioId, Long productoId) {
        return jpa.existsByUsuario_IdAndProducto_Id(usuarioId, productoId);
    }

    @Override
    public List<ListaDeseos> findByUsuarioId(Long usuarioId) {
        return jpa.findByUsuario_Id(usuarioId);
    }
}
