package com.urbancollection.ecommerce.api.web.dto;

import java.util.stream.Collectors;

import com.urbancollection.ecommerce.domain.entity.ventas.Pedido;

public class PedidoMapper {

    public static PedidoResponse toResponse(Pedido pedido) {
        if (pedido == null) return null;

        PedidoResponse dto = new PedidoResponse();

        // Identificadores básicos
        dto.setId(pedido.getId());
        dto.setUsuarioId(
                pedido.getUsuario() != null
                        ? pedido.getUsuario().getId()
                        : null
        );
        dto.setDireccionId(
                pedido.getDireccionEntrega() != null
                        ? pedido.getDireccionEntrega().getId()
                        : null
        );
        dto.setEstado(
                pedido.getEstado() != null
                        ? pedido.getEstado().name()
                        : null
        );

        //  Mapea método de pago
        dto.setMetodoPago(
                pedido.getMetodoPago() != null
                        ? pedido.getMetodoPago().name()
                        : null
        );

        // mapeamos los campos reales de subtotal, descuento, envio
        dto.setSubtotal(pedido.getSubtotal() != null ? pedido.getSubtotal() : java.math.BigDecimal.ZERO);
        dto.setDescuento(pedido.getDescuento() != null ? pedido.getDescuento() : java.math.BigDecimal.ZERO);
        dto.setEnvio(pedido.getEnvio() != null ? pedido.getEnvio() : java.math.BigDecimal.ZERO);
        dto.setTotal(pedido.getTotal() != null ? pedido.getTotal() : java.math.BigDecimal.ZERO);

        //  Calcula cantidad total de items
        if (pedido.getItems() != null && !pedido.getItems().isEmpty()) {
            int cantidadTotal = pedido.getItems().stream()
                    .mapToInt(item -> item.getCantidad())
                    .sum();
            dto.setCantidadTotal(cantidadTotal);
        } else {
            dto.setCantidadTotal(0);
        }

        // Cupón
        dto.setCuponId(pedido.getCuponId() != null ? pedido.getCuponId().longValue() : null);

        // Fecha 
        dto.setFecha(null);

        // Items
        if (pedido.getItems() != null) {
            dto.setItems(
                    pedido.getItems()
                            .stream()
                            .map(ItemPedidoMapper::toResponse)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }
}