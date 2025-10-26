package com.urbancollection.ecommerce.persistence.jpa.adapters;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.urbancollection.ecommerce.domain.entity.catalogo.Producto;
import com.urbancollection.ecommerce.domain.repository.ProductoRepository;
import com.urbancollection.ecommerce.persistence.jpa.spring.ProductoJpaRepository;

@Repository
@Transactional
public class ProductoRepositoryJpaAdapter implements ProductoRepository {

    private final ProductoJpaRepository jpa;

    public ProductoRepositoryJpaAdapter(ProductoJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    @Transactional(readOnly = true)
    public Producto findById(Long id) {
        return jpa.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> findAll() {
        return jpa.findAll();
    }

    @Override
    public Producto save(Producto producto) {
        return jpa.save(producto);
    }

    @Override
    public void delete(Long id) {
        jpa.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Producto findByNombreIgnoreCase(String nombre) {
        return jpa.findByNombreIgnoreCase(nombre).orElse(null);
    }
}
