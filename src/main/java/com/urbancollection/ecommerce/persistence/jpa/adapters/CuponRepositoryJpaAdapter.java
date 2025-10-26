package com.urbancollection.ecommerce.persistence.jpa.adapters;

import com.urbancollection.ecommerce.domain.entity.catalogo.Cupon;
import com.urbancollection.ecommerce.domain.repository.CuponRepository;
import com.urbancollection.ecommerce.persistence.jpa.spring.CuponJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * CuponRepositoryJpaAdapter
 *
 * Este adaptador conecta el dominio con JPA.
 * Implementa la interfaz CuponRepository (del dominio)
 * usando internamente un CuponJpaRepository (Spring Data JPA).
 *
 * Idea: el resto de la app habla con CuponRepository (interfaz limpia),
 * y aquí hacemos la parte concreta de base de datos.
 */
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
