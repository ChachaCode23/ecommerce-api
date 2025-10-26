// com/urbancollection/ecommerce/persistence/jpa/spring/UsuarioJpaRepository.java
package com.urbancollection.ecommerce.persistence.jpa.spring;

import com.urbancollection.ecommerce.domain.entity.usuarios.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioJpaRepository extends JpaRepository<Usuario, Long> {
    boolean existsByCorreoIgnoreCase(String correo);
    Optional<Usuario> findByCorreoIgnoreCase(String correo);
}
