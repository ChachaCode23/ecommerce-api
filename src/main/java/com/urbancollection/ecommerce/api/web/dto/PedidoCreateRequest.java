package com.urbancollection.ecommerce.api.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * PedidoCreateRequest
 * ------------------------------------------------
 * DTO de entrada para CREAR un pedido desde la API.
 * Contiene los datos minimos necesarios para armar el pedido:
 * usuarioId: quien hace el pedido.
 * direccionId: a donde se va a entregar.
 * cuponId: (opcional) si se desea aplicar un descuento.
 *  items: lista de productos con sus cantidades.
 *
 * Validaciones (Bean Validation):
 * usuarioId y direccionId son obligatorios (@NotNull).
 * items debe tener al menos un elemento (@Size(min = 1)).
 * @Valid en items: tambien valida cada ItemPedidoRequest interno
 *    (por ejemplo, que cantidad sea >= 1 y que productoId no sea null).
 */
public class PedidoCreateRequest {

    @NotNull(message = "usuarioId es obligatorio") // Debe venir el id del usuario que crea el pedido.
    private Long usuarioId;

    @NotNull(message = "direccionId es obligatorio") // Debe venir el id de la direccion de entrega.
    private Long direccionId;

    private Long cuponId;

    @Size(min = 1, message = "items no puede estar vacío") // Debe haber al menos un item en el pedido.
    @Valid // Activa validaciones en cada objeto ItemPedidoRequest de la lista.
    private List<ItemPedidoRequest> items;

    // Getters y setters son necesarios para que Spring mapee el JSON a este objeto.
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Long getDireccionId() { return direccionId; }
    public void setDireccionId(Long direccionId) { this.direccionId = direccionId; }

    public Long getCuponId() { return cuponId; }
    public void setCuponId(Long cuponId) { this.cuponId = cuponId; }

    public List<ItemPedidoRequest> getItems() { return items; }
    public void setItems(List<ItemPedidoRequest> items) { this.items = items; }
}
