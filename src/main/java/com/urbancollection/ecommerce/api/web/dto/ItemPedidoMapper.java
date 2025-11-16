package com.urbancollection.ecommerce.api.web.dto;

import java.math.BigDecimal;

import com.urbancollection.ecommerce.domain.entity.ventas.ItemPedido;

public class ItemPedidoMapper {

    public static ItemPedidoResponse toResponse(ItemPedido item) {
        if (item == null) return null;

        ItemPedidoResponse dto = new ItemPedidoResponse();

        if (item.getProducto() != null) {
            dto.setProductoId(item.getProducto().getId());
            dto.setNombreProducto(item.getProducto().getNombre());
        }

        dto.setCantidad(item.getCantidad());
        dto.setPrecioUnitario(item.getPrecioUnitario());

        BigDecimal subtotal = BigDecimal.ZERO;
        if (item.getPrecioUnitario() != null && item.getCantidad() > 0) {
            subtotal = item.getPrecioUnitario()
                    .multiply(BigDecimal.valueOf(item.getCantidad()));
        }
        dto.setSubtotal(subtotal);

        return dto;
    }
}
