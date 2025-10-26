package com.urbancollection.ecommerce.persistence.jpa.adapters;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.urbancollection.ecommerce.domain.entity.ventas.TransaccionPago;
import com.urbancollection.ecommerce.domain.repository.TransaccionPagoRepository;
import com.urbancollection.ecommerce.persistence.jpa.spring.TransaccionPagoJpaRepository;

@Repository
@Transactional
public class TransaccionPagoRepositoryJpaAdapter implements TransaccionPagoRepository {

    private final TransaccionPagoJpaRepository jpa;

    public TransaccionPagoRepositoryJpaAdapter(TransaccionPagoJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    @Transactional(readOnly = true)
    public TransaccionPago findById(Long id) {
        return jpa.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransaccionPago> findAll() {
        return jpa.findAll();
    }

    @Override
    public TransaccionPago save(TransaccionPago pago) {
        return jpa.save(pago);
    }

    @Override
    public void delete(Long id) {
        jpa.deleteById(id);
    }
}
