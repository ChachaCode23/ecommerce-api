package com.urbancollection.ecommerce.persistence.jpa.adapters;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.urbancollection.ecommerce.domain.entity.logistica.Direccion;
import com.urbancollection.ecommerce.domain.repository.DireccionRepository;
import com.urbancollection.ecommerce.persistence.jpa.spring.DireccionJpaRepository;

/**
 * DireccionRepositoryJpaAdapter
 *
 * Adaptador que implementa el repositorio del dominio (DireccionRepository)
 * usando el repositorio de Spring Data JPA (DireccionJpaRepository).
 *
 * Así el resto del sistema trabaja contra la interfaz del dominio
 * y no depende directamente de JPA.
 */
@Repository
public class DireccionRepositoryJpaAdapter implements DireccionRepository {

    private final DireccionJpaRepository jpa;

    public DireccionRepositoryJpaAdapter(DireccionJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Direccion findById(Long id) {
        return jpa.findById(id).orElse(null);
    }

    @Override
    public List<Direccion> findAll() {
        return jpa.findAll();
    }

    @Override
    public Direccion save(Direccion direccion) {
        return jpa.save(direccion);
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }

    @Override
    public Direccion findPrincipalByUsuarioId(Integer usuarioId) {
        if (usuarioId == null) return null;
        return jpa.findAll().stream()
                .filter(d -> d.getUsuarioId() != null && d.getUsuarioId().equals(usuarioId))
                .filter(d -> d.getEsPrincipal() != null && d.getEsPrincipal())
                .findFirst()
                .orElse(null);
    }
}