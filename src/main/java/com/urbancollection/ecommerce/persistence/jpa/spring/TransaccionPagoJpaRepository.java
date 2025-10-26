package com.urbancollection.ecommerce.persistence.jpa.spring;

import com.urbancollection.ecommerce.domain.entity.ventas.TransaccionPago;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * Repositorio JPA para la entidad TransaccionPago.
 * Al extender JpaRepository ya tengo CRUD completo:
 * - save()
 * - findById()
 * - findAll()
 * - deleteById()
 *
 * No defino métodos extra aquí porque por ahora con lo básico es suficiente
 * para registrar y consultar pagos.
 */
public interface TransaccionPagoJpaRepository extends JpaRepository<TransaccionPago, Long> {
}
