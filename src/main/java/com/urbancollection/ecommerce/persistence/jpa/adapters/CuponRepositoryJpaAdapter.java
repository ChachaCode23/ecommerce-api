package com.urbancollection.ecommerce.persistence.jpa.adapters;

import com.urbancollection.ecommerce.domain.entity.catalogo.Cupon;
import com.urbancollection.ecommerce.domain.repository.CuponRepository;
import com.urbancollection.ecommerce.persistence.jpa.spring.CuponJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CuponRepositoryJpaAdapter implements CuponRepository {

    private final CuponJpaRepository jpaRepository;

    public CuponRepositoryJpaAdapter(CuponJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Cupon save(Cupon cupon) {
        return jpaRepository.save(cupon);
    }

    @Override
    public Cupon findById(Long id) {
        return jpaRepository.findById(id).orElse(null);
    }

    @Override
    public List<Cupon> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
