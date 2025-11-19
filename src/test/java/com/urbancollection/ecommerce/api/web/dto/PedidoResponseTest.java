package com.urbancollection.ecommerce.api.web.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PedidoResponseTest {

    @Test
    void constructorSinArgumentos_deberiaCrearInstanciaNoNula() {
        PedidoResponse response = new PedidoResponse();
        assertNotNull(response);
    }

    @Test
    void gettersYSetters_deberianGuardarYRetornarValores() {
        PedidoResponse response = new PedidoResponse();

        BigDecimal subtotal = new BigDecimal("100.00");
        BigDecimal descuento = new BigDecimal("10.00");
        BigDecimal envio = new BigDecimal("5.00");
        BigDecimal total = new BigDecimal("95.00");
        int cantidadTotal = 3;
        Long cuponId = 123L;
        LocalDateTime fecha = LocalDateTime.now();
        List<ItemPedidoResponse> items = List.of(new ItemPedidoResponse(), new ItemPedidoResponse());

        response.setSubtotal(subtotal);
        response.setDescuento(descuento);
        response.setEnvio(envio);
        response.setTotal(total);
        response.setCantidadTotal(cantidadTotal);
        response.setCuponId(cuponId);
        response.setFecha(fecha);
        response.setItems(items);

        assertEquals(subtotal, response.getSubtotal());
        assertEquals(descuento, response.getDescuento());
        assertEquals(envio, response.getEnvio());
        assertEquals(total, response.getTotal());
        assertEquals(cantidadTotal, response.getCantidadTotal());
        assertEquals(cuponId, response.getCuponId());
        assertEquals(fecha, response.getFecha());
        assertEquals(items, response.getItems());
    }
}
