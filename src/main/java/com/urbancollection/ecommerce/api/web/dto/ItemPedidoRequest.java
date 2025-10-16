package com.urbancollection.ecommerce.api.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Aqui le damos entrada para cada item del pedido en la creacion.
 * Se usa dentro de PedidoCreateRequest (lista de items).
 *
 * Aqui hacemos las siguientes Validaciones
 * productoId: obligatorio (no puede ser null).
 * cantidad: debe ser >= 1 (no se permiten cantidades 0 o negativas).
 */
public class ItemPedidoRequest {

    @NotNull(message = "productoId es obligatorio") // Se debe enviar el id del producto a comprar.
    private Long productoId;

    @Min(value = 1, message = "cantidad debe ser >= 1") // Al menos 1 unidad por item.
    private int cantidad;

    // Getters y setters para que Spring mapee el JSON a este objeto.
    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}
