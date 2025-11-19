package com.urbancollection.ecommerce.api.web.dto;

import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class DespachoRequestTest {

    @Test
    void constructorSinArgumentos_deberiaCrearInstanciaNoNula() {
        DespachoRequest request = new DespachoRequest();
        assertNotNull(request);
    }

    @Test
    void gettersYSetters_deberianPermitirAsignarYLeerTracking() {
        DespachoRequest request = new DespachoRequest();
        String tracking = "ABC123-TRACK";

        request.setTracking(tracking);

        assertEquals(tracking, request.getTracking());
    }

    @Test
    void campoTracking_deberiaEstarAnotadoConNotBlank() throws NoSuchFieldException {
        Field trackingField = DespachoRequest.class.getDeclaredField("tracking");
        NotBlank notBlank = trackingField.getAnnotation(NotBlank.class);
        assertNotNull(notBlank, "El campo 'tracking' debe estar anotado con @NotBlank");
    }
}
