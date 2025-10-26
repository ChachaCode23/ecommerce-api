package com.urbancollection.ecommerce.api.web.dto;

import com.urbancollection.ecommerce.domain.enums.MetodoDePago;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * ConfirmarPagoRequest
 *
 * Este objeto (DTO) es lo que el cliente tiene que mandar cuando quiere
 * confirmar el pago de un pedido. Se usa en:
 *
 *   POST /api/pedidos/{id}/pago
 *
 * aquí el cliente me dice:
 *  - con qué método está pagando
 *  - cuánto pagó
 *  - y opcionalmente me manda una clave de idempotencia
 *
 * Importante:
 * - Este DTO NO tiene lógica de negocio. No calcula nada, no guarda nada.
 *   Su trabajo es solamente transportar los datos desde el JSON del request
 *   hasta el controlador.
 *
 * - Las validaciones con anotaciones (@NotNull, @DecimalMin, etc.)
 *   se usan para asegurar que el request venga con información válida
 *   antes de llegar al servicio.
 *
 *   Si algo no cumple, Spring lo bloquea y devolvemos 400 Bad Request.
 *
 * Campos:
 *   metodo           -> tipo de pago (tarjeta, transferencia, etc.)
 *   monto            -> cuánto se cobró
 *   idempotencyKey   -> (opcional) una llave para evitar pagos duplicados
 *                       si el cliente reintenta la misma operación
 *                       por ejemplo porque se le cayó el internet.
 */
public class ConfirmarPagoRequest {

    /**
     * metodo:
     * - @NotNull → el cliente TIENE que decir con qué método está pagando.
     *
     * Esto es un enum MetodoDePago en el dominio, o sea, no acepto cualquier string.
     * Solamente métodos válidos como TARJETA, EFECTIVO, TRANSFERENCIA, etc.
     *
     * Ejemplo JSON:
     *   "metodo": "TARJETA"
     */
    @NotNull   // Debe venir un metodo de pago válido, no se acepta null.
    private MetodoDePago metodo;

    /**
     * monto:
     * - @NotNull → es obligatorio mandar el monto.
     * - @DecimalMin("0.01") → no acepto montos en 0 ni negativos.
     *
     * Uso BigDecimal porque estamos manejando dinero.
     * No uso double para no tener problemas de precisión con decimales.
     *
     * Ejemplo JSON:
     *   "monto": 107.98
     *
     * Si el cliente manda "monto": 0 o -5,
     * automáticamente eso rompe la validación y devolvemos 400.
     */
    @NotNull // El monto es requerido (no puede faltar).
    @DecimalMin("0.01") // El monto tiene que ser mayor o igual a 0.01.
    private BigDecimal monto;

    /**
     * idempotencyKey:
     * Este campo es opcional.
     *
     * En pagos tú no quieres cobrar dos veces si el cliente sin querer
     * manda el mismo request más de una vez (por lag, o porque le dio doble click).
     *
     * Entonces el cliente puede mandar una "idempotencyKey".
     * Ejemplo:
     *   "idempotencyKey": "pago-usuario123-orden99-intento1"
     *
     * - Si llega otra vez la misma clave, tú detectas que es el mismo pago repetido
     *   y no vuelves a procesar el cobro.
     *
     * Eso ayuda a evitar pagos duplicados.
     * (Esto es súper común en APIs de pago reales tipo Stripe.)
     */
    private String idempotencyKey;

    // Getters y setters:
    // Spring usa estos métodos para convertir el JSON del request
    // en una instancia de esta clase automáticamente.
    public MetodoDePago getMetodo() { return metodo; }
    public void setMetodo(MetodoDePago metodo) { this.metodo = metodo; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
}
