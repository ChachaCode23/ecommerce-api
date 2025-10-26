package com.urbancollection.ecommerce.api.web.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public class PedidoCreateRequest {

    @NotNull
    private Long usuarioId;

    @NotNull
    private Long direccionId;

    // puede ser null
    private Long cuponId;

    @NotNull
    private List<ItemPedidoRequest> items;

    // ===== GETTERS & SETTERS =====

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
    // Clase anidada para cada item
    // =============================
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
