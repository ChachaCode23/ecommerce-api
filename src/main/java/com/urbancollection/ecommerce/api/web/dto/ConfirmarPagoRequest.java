package com.urbancollection.ecommerce.api.web.dto;

import com.urbancollection.ecommerce.domain.enums.MetodoDePago;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * ConfirmarPagoRequest
 * ------------------------------------------------
 * DTO de entrada para confirmar el pago de un pedido.
 * - Se usa en el endpoint POST /api/pedidos/{id}/pago.
 * - Incluye el metodo de pago y el monto a pagar.
 *
 * Validaciones (Bean Validation):
 * - @NotNull en 'metodo' y 'monto' para requerir ambos campos.
 * - @DecimalMin("0.01") en 'monto' para evitar valores 0 o negativos.
 *
 * Nota (como estudiante):
 * - Este DTO no contiene logica, solo datos que envia el cliente.
 * - El controller usa @Valid para activar estas validaciones automaticamente.
 */
public class ConfirmarPagoRequest {

    @NotNull   // Debe venir un metodo de pago valido (no null).
    private MetodoDePago metodo;

    @NotNull // El monto es requerido...
    @DecimalMin("0.01")    // el monto debe ser mayor o igual a 0.01, no 0 ni negativo.
    private BigDecimal monto;

 // + campo opcional
    private String idempotencyKey;


    // Getters y setters para que Spring mapee el JSON a este objeto
    public MetodoDePago getMetodo() { return metodo; }
    public void setMetodo(MetodoDePago metodo) { this.metodo = metodo; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    
    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
}
