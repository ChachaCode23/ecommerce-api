package com.urbancollection.ecommerce.persistence.jpa.adapters;

import com.urbancollection.ecommerce.domain.entity.logistica.Envio;
import com.urbancollection.ecommerce.domain.repository.EnvioRepository;
import com.urbancollection.ecommerce.persistence.jpa.spring.EnvioJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * EnvioRepositoryJpaAdapter
 *
 * Este adaptador conecta el repositorio del dominio (EnvioRepository)
 * con la implementación real en base de datos (EnvioJpaRepository de Spring Data JPA).
 *
 * Así el resto de la aplicación usa EnvioRepository (interfaz limpia),
 * sin tener que saber que por debajo hay JPA.
 */
@Repository
public class EnvioRepositoryJpaAdapter implements EnvioRepository {

    private final EnvioJpaRepository jpaRepository;

    public EnvioRepositoryJpaAdapter(EnvioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Envio save(Envio envio) {
        return jpaRepository.save(envio);
    }

    @Override
    public Envio findById(Long id) {
        return jpaRepository.findById(id).orElse(null);
    }

    @Override
    public List<Envio> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
