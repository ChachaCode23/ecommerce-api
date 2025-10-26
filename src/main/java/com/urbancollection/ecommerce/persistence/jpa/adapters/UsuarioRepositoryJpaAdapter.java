package com.urbancollection.ecommerce.persistence.jpa.adapters;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.urbancollection.ecommerce.domain.entity.usuarios.Usuario;
import com.urbancollection.ecommerce.domain.repository.UsuarioRepository;
import com.urbancollection.ecommerce.persistence.jpa.spring.UsuarioJpaRepository;

@Repository
@Transactional
public class UsuarioRepositoryJpaAdapter implements UsuarioRepository {

    private final UsuarioJpaRepository jpa;

    public UsuarioRepositoryJpaAdapter(UsuarioJpaRepository jpa) {
        this.jpa = jpa;
    }

    // ===== CRUD básico =====

    @Override
    @Transactional(readOnly = true)
    public Usuario findById(Long id) {
        return jpa.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        return jpa.findAll();
    }

    @Override
    public Usuario save(Usuario usuario) {
        return jpa.save(usuario);
    }

    @Override
    public void delete(Long id) {
        jpa.deleteById(id);
    }

    // ===== métodos específicos de dominio =====

    @Override
    @Transactional(readOnly = true)
    public Usuario findByCorreoIgnoreCase(String correo) {
        // dominio espera null si no existe
        return jpa.findByCorreoIgnoreCase(correo).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCorreoIgnoreCase(String correo) {
        return jpa.existsByCorreoIgnoreCase(correo);
    }
}
