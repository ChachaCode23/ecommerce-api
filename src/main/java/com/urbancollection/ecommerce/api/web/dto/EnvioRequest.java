package com.urbancollection.ecommerce.api.web.dto;

import com.urbancollection.ecommerce.domain.enums.EstadoDeEnvio;
import jakarta.validation.constraints.*;

/**
 * EnvioRequest
 * DTO para recibir datos de envío desde la API (crear/actualizar).
 */
public class EnvioRequest {

    @NotNull(message = "El pedido es obligatorio")
    private Long pedidoId;

    @NotBlank(message = "El tracking es obligatorio")
    @Size(max = 100, message = "El tracking no debe superar 100 caracteres")
    private String tracking;

    @NotNull(message = "El estado de envío es obligatorio")
    private EstadoDeEnvio estado;

    // Getters y setters
    public Long getPedidoId() { return pedidoId; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }

    public String getTracking() { return tracking; }
    public void setTracking(String tracking) { this.tracking = tracking; }

    public EstadoDeEnvio getEstado() { return estado; }
    public void setEstado(EstadoDeEnvio estado) { this.estado = estado; }
}