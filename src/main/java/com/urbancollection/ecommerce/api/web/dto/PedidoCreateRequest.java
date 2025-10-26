package com.urbancollection.ecommerce.api.web.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * PedidoCreateRequest
 *
 * Este DTO es el body que mando cuando quiero crear un pedido nuevo.
 * Se usa en el endpoint POST /api/pedidos.
 *
 * Campos:
 * - usuarioId: quién está haciendo el pedido. Obligatorio.
 * - direccionId: adónde se va a enviar. Obligatorio.
 * - cuponId: cupón aplicado (puede venir null).
 * - items: lista de productos que el usuario quiere comprar. Obligatorio.
 *
 * Validaciones básicas:
 * - @NotNull en usuarioId, direccionId e items para asegurar que vengan esos datos.
 * - Cada item de la lista tiene su propio productoId y cantidad.
 */
public class PedidoCreateRequest {

    @NotNull
    private Long usuarioId;

    @NotNull
    private Long direccionId;

    // opcional
    private Long cuponId;

    @NotNull
    private List<ItemPedidoRequest> items;

    // GETTERS & SETTERS
    public Long getUsuarioId() {
        return usuarioId;
    }
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getDireccionId() {
        return direccionId;
    }
    public void setDireccionId(Long direccionId) {
        this.direccionId = direccionId;
    }

    public Long getCuponId() {
        return cuponId;
    }
    public void setCuponId(Long cuponId) {
        this.cuponId = cuponId;
    }

    public List<ItemPedidoRequest> getItems() {
        return items;
    }
    public void setItems(List<ItemPedidoRequest> items) {
        this.items = items;
    }

    // =============================
    // Clase interna para cada item
    // =============================

    /**
     * ItemPedidoRequest interno:
     * Representa 1 producto dentro del pedido.
     *
     * - productoId: qué producto se está comprando.
     * - cantidad: cuántas unidades de ese producto.
     *
     * Ambos marcados como @NotNull para forzar que vengan en el request.
     */
    public static class ItemPedidoRequest {

        @NotNull
        private Long productoId;

        @NotNull
        private Integer cantidad;

        public Long getProductoId() {
            return productoId;
        }
        public void setProductoId(Long productoId) {
            this.productoId = productoId;
        }

        public Integer getCantidad() {
            return cantidad;
        }
        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }
    }
}
