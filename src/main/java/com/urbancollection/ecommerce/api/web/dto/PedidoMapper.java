package com.urbancollection.ecommerce.api.web.dto;

import java.math.BigDecimal;
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

        // Totales: por ahora usamos total como subtotal y 0 para descuento/envío.
        // Más adelante, si el dominio expone subtotal/descuento/envío separados,
        // se mapea desde allí.
        BigDecimal total = pedido.getTotal();
        dto.setTotal(total);
        dto.setSubtotal(total);
        dto.setDescuento(BigDecimal.ZERO);
        dto.setEnvio(BigDecimal.ZERO);

        // Cupón y fecha: se dejan null si aún no los tenemos en la entidad.
        // dto.setCuponId(...);
        // dto.setFecha(...);

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