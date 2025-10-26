package com.urbancollection.ecommerce.persistence.jpa.spring;

import com.urbancollection.ecommerce.domain.entity.ventas.ListaDeseos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListaDeseosJpaRepository extends JpaRepository<ListaDeseos, Long> {

    boolean existsByUsuario_IdAndProducto_Id(Long usuarioId, Long productoId);

    List<ListaDeseos> findByUsuario_Id(Long usuarioId);
}
