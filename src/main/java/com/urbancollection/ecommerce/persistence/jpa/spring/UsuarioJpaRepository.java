package com.urbancollection.ecommerce.persistence.jpa.spring;

import com.urbancollection.ecommerce.domain.entity.usuarios.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioJpaRepository extends JpaRepository<Usuario, Long> {

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Usuario u WHERE UPPER(u.correo) = UPPER(:correo)")
    boolean existsByCorreoIgnoreCase(@Param("correo") String correo);

    @Query("SELECT u FROM Usuario u WHERE UPPER(u.correo) = UPPER(:correo)")
    Optional<Usuario> findByCorreoIgnoreCase(@Param("correo") String correo);
    
    // ✅ Alias para compatibilidad con WebControllers
    default Optional<Usuario> findByEmail(String email) {
        return findByCorreoIgnoreCase(email);
    }
}