package com.urbancollection.ecommerce.api.web.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DespachoRequest
 *
 * Este DTO es el body que mando cuando quiero marcar un pedido como despachado.
 * Se usa en el endpoint: POST /api/pedidos/{id}/despacho
 *
 * Solo necesito pasar el tracking del envío.
 *
 * Validación:
 * - @NotBlank en tracking: obliga a mandar un texto real (no null, no vacío, no solo espacios).
 */
public class DespachoRequest {

    @NotBlank // Requiero que el tracking venga con un valor válido
    private String tracking;

    // Getters y setters para que Spring pueda mapear el JSON del request
    public String getTracking() { return tracking; }
    public void setTracking(String tracking) { this.tracking = tracking; }
}
