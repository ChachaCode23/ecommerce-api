// com/urbancollection/ecommerce/persistence/jpa/spring/UsuarioJpaRepository.java
package com.urbancollection.ecommerce.persistence.jpa.spring;

import com.urbancollection.ecommerce.domain.entity.usuarios.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 *
 * Repositorio Spring Data JPA para la entidad Usuario.
 * JpaRepository ya me da el CRUD básico.
 *
 * defino dos métodos específicos basados en el correo:
 *
 * - existsByCorreoIgnoreCase(String correo):
 *   true/false si ya hay un usuario registrado con ese correo (sin importar mayúsculas).
 *   Sirve para validar que no se repita el email al crear la cuenta.
 *
 * - findByCorreoIgnoreCase(String correo):
 *   busca un usuario por correo, ignorando mayúsculas/minúsculas.
 *   Devuelve Optional<Usuario>.
 *   Se usa, por ejemplo, en login / autenticación.
 */
@Repository
public interface UsuarioJpaRepository extends JpaRepository<Usuario, Long> {

    boolean existsByCorreoIgnoreCase(String correo);

    Optional<Usuario> findByCorreoIgnoreCase(String correo);
}
