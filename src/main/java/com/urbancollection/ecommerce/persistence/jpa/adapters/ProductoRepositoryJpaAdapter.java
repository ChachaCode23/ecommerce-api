package com.urbancollection.ecommerce.persistence.jpa.adapters;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.urbancollection.ecommerce.domain.entity.catalogo.Producto;
import com.urbancollection.ecommerce.domain.repository.ProductoRepository;
import com.urbancollection.ecommerce.persistence.jpa.spring.ProductoJpaRepository;

/**
 * ProductoRepositoryJpaAdapter
 *
 * Adaptador que implementa la interfaz del dominio (ProductoRepository)
 * usando el repositorio Spring Data JPA (ProductoJpaRepository).
 *
 * Así el dominio trabaja con ProductoRepository sin depender directamente de JPA.
 */
@Repository
@Transactional
public class ProductoRepositoryJpaAdapter implements ProductoRepository {

    private final ProductoJpaRepository jpa;

    public ProductoRepositoryJpaAdapter(ProductoJpaRepository jpa) {
        this.jpa = jpa;
    }

    /**
     * findById:
     * Busca un producto por id. Devuelve null si no existe.
     * Solo lectura.
     */
    @Override
    @Transactional(readOnly = true)
    public Producto findById(Long id) {
        return jpa.findById(id).orElse(null);
    }

    /**
     * findAll:
     * Devuelve todos los productos.
     * Solo lectura.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Producto> findAll() {
        return jpa.findAll();
    }

    /**
     * Inserta o actualiza un producto en la base de datos.
     */
    @Override
    public Producto save(Producto producto) {
        return jpa.save(producto);
    }

    /**
     * delete:
     * Elimina un producto por id.
     */
    @Override
    public void delete(Long id) {
        jpa.deleteById(id);
    }

    /**
     * findByNombreIgnoreCase:
     * Busca un producto por nombre sin importar mayúsculas/minúsculas.
     * Devuelve null si no lo encuentra.
     * Esto se puede usar para validar nombres duplicados.
     */
    @Override
    @Transactional(readOnly = true)
    public Producto findByNombreIgnoreCase(String nombre) {
        return jpa.findByNombreIgnoreCase(nombre).orElse(null);
    }
}
