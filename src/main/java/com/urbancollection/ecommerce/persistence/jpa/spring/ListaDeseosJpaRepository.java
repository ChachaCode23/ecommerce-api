package com.urbancollection.ecommerce.persistence.jpa.spring;

import com.urbancollection.ecommerce.domain.entity.ventas.ListaDeseos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ListaDeseosJpaRepository
 *
 * Repositorio JPA directo para la entidad ListaDeseos.
 * Spring Data genera la implementación automáticamente.
 *
 * Además del CRUD básico que ya da JpaRepository,
 * defino dos consultas personalizadas:
 *
 * - existsByUsuario_IdAndProducto_Id:
 *   devuelve true si un usuario ya tiene un producto en su lista de deseos.
 *   Sirve para no duplicar el mismo producto.
 *
 * - findByUsuario_Id:
 *   devuelve toda la lista de deseos de un usuario específico.
 */
@Repository
public interface ListaDeseosJpaRepository extends JpaRepository<ListaDeseos, Long> {

    boolean existsByUsuario_IdAndProducto_Id(Long usuarioId, Long productoId);

    List<ListaDeseos> findByUsuario_Id(Long usuarioId);
}
