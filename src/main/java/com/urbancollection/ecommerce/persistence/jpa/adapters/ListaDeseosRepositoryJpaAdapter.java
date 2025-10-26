package com.urbancollection.ecommerce.persistence.jpa.adapters;

import com.urbancollection.ecommerce.domain.entity.ventas.ListaDeseos;
import com.urbancollection.ecommerce.domain.repository.ListaDeseosRepository;
import com.urbancollection.ecommerce.persistence.jpa.spring.ListaDeseosJpaRepository;

import java.util.List;

/**
 * ListaDeseosRepositoryJpaAdapter
 *
 * Adaptador que conecta la interfaz del dominio (ListaDeseosRepository)
 * con la implementación real en base de datos (ListaDeseosJpaRepository de Spring Data JPA).
 *
 * Se usa para manejar la lista de deseos (wishlist) de cada usuario.
 * Aquí tenemos operaciones típicas CRUD y también consultas más específicas
 * como buscar por usuario o verificar si un producto ya está en su wishlist.
 */
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

    /**
     * existsByUsuarioIdAndProductoId:
     * Devuelve true si ese usuario ya tiene ese producto en su lista de deseos.
     * Sirve para evitar duplicados.
     */
    @Override
    public boolean existsByUsuarioIdAndProductoId(Long usuarioId, Long productoId) {
        return jpa.existsByUsuario_IdAndProducto_Id(usuarioId, productoId);
    }

    /**
     * findByUsuarioId:
     * Devuelve todos los productos que un usuario tiene en su wishlist.
     */
    @Override
    public List<ListaDeseos> findByUsuarioId(Long usuarioId) {
        return jpa.findByUsuario_Id(usuarioId);
    }
}
