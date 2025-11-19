package com.urbancollection.ecommerce.api.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ConfirmarPagoRequestTest {

    @Test
    void constructorSinArgumentos_deberiaCrearInstanciaNoNula() {
        ConfirmarPagoRequest request = new ConfirmarPagoRequest();
        assertNotNull(request);
    }

    @Test
    void gettersYSetters_deberianPermitirAsignarYLeerValoresBasicos() {
        ConfirmarPagoRequest request = new ConfirmarPagoRequest();

        BigDecimal monto = new BigDecimal("123.45");
        String idempotencyKey = "pago-123";

        request.setMonto(monto);
        request.setIdempotencyKey(idempotencyKey);
        // Para metodo no usamos un valor concreto porque el enum no está en este módulo de test

        assertEquals(monto, request.getMonto());
        assertEquals(idempotencyKey, request.getIdempotencyKey());
    }

    @Test
    void campoMetodo_deberiaTenerNotNull() throws NoSuchFieldException {
        Field metodoField = ConfirmarPagoRequest.class.getDeclaredField("metodo");
        NotNull notNull = metodoField.getAnnotation(NotNull.class);
        assertNotNull(notNull, "El campo 'metodo' debe estar anotado con @NotNull");
    }

    @Test
    void campoMonto_deberiaTenerNotNullYDecimalMin001() throws NoSuchFieldException {
        Field montoField = ConfirmarPagoRequest.class.getDeclaredField("monto");

        NotNull notNull = montoField.getAnnotation(NotNull.class);
        assertNotNull(notNull, "El campo 'monto' debe estar anotado con @NotNull");

        DecimalMin decimalMin = montoField.getAnnotation(DecimalMin.class);
        assertNotNull(decimalMin, "El campo 'monto' debe estar anotado con @DecimalMin");
        assertEquals("0.01", decimalMin.value(), "El valor mínimo del monto debe ser 0.01");
    }
}
