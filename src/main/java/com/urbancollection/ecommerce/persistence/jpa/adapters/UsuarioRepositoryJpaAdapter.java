package com.urbancollection.ecommerce.persistence.jpa.adapters;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.urbancollection.ecommerce.domain.entity.usuarios.Usuario;
import com.urbancollection.ecommerce.domain.repository.UsuarioRepository;
import com.urbancollection.ecommerce.persistence.jpa.spring.UsuarioJpaRepository;

/**
 * UsuarioRepositoryJpaAdapter
 *
 * Adaptador que conecta la interfaz del dominio (UsuarioRepository)
 * con el repositorio real de JPA (UsuarioJpaRepository).
 *
 * Así el dominio trabaja con UsuarioRepository sin depender directamente de JPA/Spring Data.
 * Aquí resuelvo CRUD básico y también consultas específicas del usuario como buscar por correo.
 */
@Repository
@Transactional
public class UsuarioRepositoryJpaAdapter implements UsuarioRepository {

    private final UsuarioJpaRepository jpa;

    public UsuarioRepositoryJpaAdapter(UsuarioJpaRepository jpa) {
        this.jpa = jpa;
    }

    // ===== CRUD básico =====

    /**
     * findById:
     * Busca un usuario por id. Devuelve null si no existe.
     * Solo lectura.
     */
    @Override
    @Transactional(readOnly = true)
    public Usuario findById(Long id) {
        return jpa.findById(id).orElse(null);
    }

    /**
     * findAll:
     * Devuelve todos los usuarios.
     * Solo lectura.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        return jpa.findAll();
    }

    /**
     * save:
     * Crea o actualiza un usuario.
     */
    @Override
    public Usuario save(Usuario usuario) {
        return jpa.save(usuario);
    }

    /**
     * Elimina un usuario por id.
     */
    @Override
    public void delete(Long id) {
        jpa.deleteById(id);
    }

    // ===== métodos específicos de dominio =====

    /**
     * findByCorreoIgnoreCase:
     * Busca un usuario por correo.
     * Devuelve null si no existe.
     * Esto sirve para login / validación de cuenta duplicada.
     */
    @Override
    @Transactional(readOnly = true)
    public Usuario findByCorreoIgnoreCase(String correo) {
        return jpa.findByCorreoIgnoreCase(correo).orElse(null);
    }

    /**
     * existsByCorreoIgnoreCase:
     * Devuelve true si ya hay un usuario registrado con ese correo.
     * Lo uso para evitar crear dos cuentas con el mismo email.
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsByCorreoIgnoreCase(String correo) {
        return jpa.existsByCorreoIgnoreCase(correo);
    }
}
