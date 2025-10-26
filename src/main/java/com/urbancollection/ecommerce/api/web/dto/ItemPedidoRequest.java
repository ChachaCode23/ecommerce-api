package com.urbancollection.ecommerce.api.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * ItemPedidoRequest
 *
 * Representa un ítem dentro del pedido al momento de crearlo.
 * Esto forma parte de la lista de productos que el usuario quiere comprar.
 *
 * Validaciones:
 * - productoId: obligatorio (@NotNull). Debe venir el ID del producto.
 * - cantidad: mínimo 1 (@Min(1)). No se aceptan cantidades 0 o negativas.
 */
public class ItemPedidoRequest {

    @NotNull(message = "productoId es obligatorio")
    private Long productoId;

    @Min(value = 1, message = "cantidad debe ser >= 1")
    private int cantidad;

    // Getters y setters para que Spring haga el binding del JSON
    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}
