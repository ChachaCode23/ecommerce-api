package com.urbancollection.ecommerce.persistence.jpa.adapters;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.urbancollection.ecommerce.domain.entity.ventas.TransaccionPago;
import com.urbancollection.ecommerce.domain.repository.TransaccionPagoRepository;
import com.urbancollection.ecommerce.persistence.jpa.spring.TransaccionPagoJpaRepository;

/**
 * TransaccionPagoRepositoryJpaAdapter
 *
 * Adaptador que implementa la interfaz del dominio (TransaccionPagoRepository)
 * usando el repositorio Spring Data JPA real (TransaccionPagoJpaRepository).
 *
 * Aquí hacemos CRUD básico sobre las transacciones de pago
 * sin que la capa de negocio dependa directamente de JPA.
 */
@Repository
@Transactional
public class TransaccionPagoRepositoryJpaAdapter implements TransaccionPagoRepository {

    private final TransaccionPagoJpaRepository jpa;

    public TransaccionPagoRepositoryJpaAdapter(TransaccionPagoJpaRepository jpa) {
        this.jpa = jpa;
    }

    /**
     * Busca una transacción de pago por id. Devuelve null si no existe.
     * Marcado como readOnly porque es solo lectura.
     */
    @Override
    @Transactional(readOnly = true)
    public TransaccionPago findById(Long id) {
        return jpa.findById(id).orElse(null);
    }

    /**
     * Devuelve todas las transacciones registradas.
     * Solo lectura.
     */
    @Override
    @Transactional(readOnly = true)
    public List<TransaccionPago> findAll() {
        return jpa.findAll();
    }

    /**
     * Guarda o actualiza una transacción de pago.
     * Se usa cuando se registra un pago confirmado.
     */
    @Override
    public TransaccionPago save(TransaccionPago pago) {
        return jpa.save(pago);
    }

    /**
     * Elimina una transacción de pago por id.
     */
    @Override
    public void delete(Long id) {
        jpa.deleteById(id);
    }
}
