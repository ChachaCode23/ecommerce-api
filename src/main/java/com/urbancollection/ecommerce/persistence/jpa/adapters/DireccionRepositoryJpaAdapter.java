package com.urbancollection.ecommerce.persistence.jpa.adapters;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.urbancollection.ecommerce.domain.entity.logistica.Direccion;
import com.urbancollection.ecommerce.domain.repository.DireccionRepository;
import com.urbancollection.ecommerce.persistence.jpa.spring.DireccionJpaRepository;

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
}
