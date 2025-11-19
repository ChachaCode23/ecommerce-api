package com.urbancollection.ecommerce.api.web.dto;

import java.math.BigDecimal;

import com.urbancollection.ecommerce.domain.entity.ventas.ItemPedido;


public class ItemPedidoMapper {

    // Convierte un ItemPedido en un ItemPedidoResponse.
    // Aquí solo mapeo los campos que necesito mostrar al usuario o al frontend.
    public static ItemPedidoResponse toResponse(ItemPedido item) {
        if (item == null) return null;

        ItemPedidoResponse dto = new ItemPedidoResponse();

        // Si el item tiene producto, copio el id y el nombre del producto al DTO.
        if (item.getProducto() != null) {
            dto.setProductoId(item.getProducto().getId());
            dto.setNombreProducto(item.getProducto().getNombre());
        }

        // Copio la cantidad y el precio unitario tal cual vienen de la entidad.
        dto.setCantidad(item.getCantidad());
        dto.setPrecioUnitario(item.getPrecioUnitario());

        // Calculo el subtotal del item = precioUnitario * cantidad.
        // Si el precio es null o la cantidad no es válida, dejo el subtotal en 0.
        BigDecimal subtotal = BigDecimal.ZERO;
        if (item.getPrecioUnitario() != null && item.getCantidad() > 0) {
            subtotal = item.getPrecioUnitario()
                    .multiply(BigDecimal.valueOf(item.getCantidad()));
        }
        dto.setSubtotal(subtotal);

        return dto;
    }
}
