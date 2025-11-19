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
 * Implementa la interfaz CuponRepository del dominio
 * usando internamente un CuponJpaRepository
 * aquí hacemos la parte concreta de base de datos.
 */
@Repository
public class CuponRepositoryJpaAdapter implements CuponRepository {

    // Referencia al repositorio JPA que sabe trabajar directamente con la base de datos.
    private final CuponJpaRepository jpaRepository;

    // En el constructor inyecta el CuponJpaRepository que proporciona Spring.
    public CuponRepositoryJpaAdapter(CuponJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    // Guarda o actualiza un cupón en la base de datos usando JPA.
    public Cupon save(Cupon cupon) {
        return jpaRepository.save(cupon);
    }

    @Override
    // Busca un cupón por su id. Si no existe, devuelve null.
    public Cupon findById(Long id) {
        return jpaRepository.findById(id).orElse(null);
    }

    @Override
    // Devuelve la lista completa de cupones almacenados en la base de datos.
    public List<Cupon> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    // Elimina un cupón por su id usando el repositorio JPA.
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
