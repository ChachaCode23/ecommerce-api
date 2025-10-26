package com.urbancollection.ecommerce.persistence.jpa.spring;

import com.urbancollection.ecommerce.domain.entity.ventas.TransaccionPago;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransaccionPagoJpaRepository extends JpaRepository<TransaccionPago, Long> {
}
