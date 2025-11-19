package com.urbancollection.ecommerce.api.web.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.urbancollection.ecommerce.domain.entity.catalogo.Producto;
import com.urbancollection.ecommerce.domain.entity.ventas.ItemPedido;
import com.urbancollection.ecommerce.domain.entity.ventas.Pedido;

class PedidoMapperTest {

    @Test
    void toResponse_whenPedidoIsNull_returnsNull() {
        // Act
        PedidoResponse result = PedidoMapper.toResponse(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toResponse_mapsFieldsAndItemsCorrectly() {
        // Arrange
        Pedido pedido = new Pedido();

        // Totales
        BigDecimal subtotal = new BigDecimal("150.00");
        BigDecimal descuento = new BigDecimal("10.00");
        BigDecimal envio = new BigDecimal("5.00");
        BigDecimal total = new BigDecimal("145.00");

        pedido.setSubtotal(subtotal);
        pedido.setDescuento(descuento);
        pedido.setEnvio(envio);
        pedido.setTotal(total);

        // Cupón
        pedido.setCuponId(123);

        // Producto base para los items
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Producto de prueba");
        producto.setPrecio(new BigDecimal("20.00"));
        producto.setStock(100);
        producto.setSku("SKU-TEST");

        // Item 1 (cantidad 2)
        ItemPedido item1 = new ItemPedido();
        item1.setProducto(producto);
        item1.setCantidad(2);
        item1.setPrecioUnitario(new BigDecimal("20.00"));
        item1.setPedido(pedido);

        // Item 2 (cantidad 3)
        ItemPedido item2 = new ItemPedido();
        item2.setProducto(producto);
        item2.setCantidad(3);
        item2.setPrecioUnitario(new BigDecimal("20.00"));
        item2.setPedido(pedido);

        // Agregar items al pedido
        pedido.getItems().add(item1);
        pedido.getItems().add(item2);

        // Act
        PedidoResponse dto = PedidoMapper.toResponse(pedido);

        // Assert
        assertNotNull(dto);

        // Totales mapeados
        assertEquals(subtotal, dto.getSubtotal());
        assertEquals(descuento, dto.getDescuento());
        assertEquals(envio, dto.getEnvio());
        assertEquals(total, dto.getTotal());

        // Cantidad total de items = 2 + 3 = 5
        assertEquals(5, dto.getCantidadTotal());

        // Cupón convertido de Integer a Long
        assertEquals(123L, dto.getCuponId());

        // Fecha la deja en null por ahora
        assertNull(dto.getFecha());

        // Items mapeados (solo revisamos tamaño para no acoplar al ItemPedidoMapper)
        assertNotNull(dto.getItems());
        assertEquals(2, dto.getItems().size());
    }

    @Test
    void toResponse_whenNumericFieldsAreNull_usesZeroDefaults() {
        // Arrange
        Pedido pedido = new Pedido();

        // Forzamos los campos a null para probar los defaults
        pedido.setSubtotal(null);
        pedido.setDescuento(null);
        pedido.setEnvio(null);
        pedido.setTotal(null);

        // No agregamos items ni cupón

        // Act
        PedidoResponse dto = PedidoMapper.toResponse(pedido);

        // Assert
        assertNotNull(dto);

        // Defaults de BigDecimal.ZERO
        assertEquals(BigDecimal.ZERO, dto.getSubtotal());
        assertEquals(BigDecimal.ZERO, dto.getDescuento());
        assertEquals(BigDecimal.ZERO, dto.getEnvio());
        assertEquals(BigDecimal.ZERO, dto.getTotal());

        // Sin items → cantidadTotal = 0
        assertEquals(0, dto.getCantidadTotal());

        // Sin cupón → cuponId = null
        assertNull(dto.getCuponId());
    }
}
