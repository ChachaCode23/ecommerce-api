package com.urbancollection.ecommerce.api.web.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada para registrar el despacho de un pedido.
 * Se usa en: POST /api/pedidos/{id}/despacho
 * Solo necesita el numero de seguimiento (tracking).
 *
 * Validaciones:
 * @NotBlank en 'tracking' para exigir que venga con texto, no null, no vacio, no solo espacios.
 */
public class DespachoRequest {

    @NotBlank   // Debe contener algun valor no vacio ni espacios.
    private String tracking;

    // Getter y setter requeridos para que Spring mapee el JSON a este objeto.
    public String getTracking() { return tracking; }
    public void setTracking(String tracking) { this.tracking = tracking; }
}
