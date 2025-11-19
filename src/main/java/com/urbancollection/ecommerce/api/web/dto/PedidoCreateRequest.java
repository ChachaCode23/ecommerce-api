package com.urbancollection.ecommerce.api.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

// DTO que uso para recibir la información cuando el cliente crea un pedido.
public class PedidoCreateRequest {

    // Id del usuario que está creando el pedido (obligatorio).
    @NotNull
    private Long usuarioId;

    // Id de la dirección de envío seleccionada por el usuario (obligatorio).
    @NotNull
    private Long direccionId;

    // Id del cupón que el usuario quiere aplicar al pedido (opcional).
    private Long cuponId;

    // Lista de items que forman parte del pedido, cada uno con producto y cantidad.
    @Valid
    @NotNull
    private List<ItemPedidoRequest> items;

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

    // Clase interna para representar cada item del pedido que llega en la petición.
    public static class ItemPedidoRequest {

        // Id del producto que el usuario quiere comprar (obligatorio).
        @NotNull
        private Long productoId;

        // Cantidad de unidades de ese producto (obligatoria y debe ser mayor que 0).
        @NotNull
        @Min(value = 1, message = "La cantidad debe ser mayor que 0")
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
