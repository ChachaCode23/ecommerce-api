package com.urbancollection.ecommerce.api.web.dto;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ApiErrorResponseTest {

    @Test
    // Verifica que el constructor asigne bien los valores
    // y que los getters devuelvan exactamente lo que se pasó.
    void constructor_yGetters_deberianRetornarLosValoresAsignados() {
        // given: preparo un mensaje de error y una lista de detalles
        String error = "Validación fallida";
        List<String> details = Arrays.asList(
                "El campo nombre es obligatorio",
                "El precio debe ser mayor a 0"
        );

        //  creo el objeto ApiErrorResponse 
        ApiErrorResponse response = new ApiErrorResponse(error, details);

        // compruebo que los getters devuelvan lo mismo que pasé al constructor
        assertEquals(error, response.getError());
        assertEquals(details, response.getDetails());
        assertSame(details, response.getDetails()); // misma referencia de lista
    }

    @Test
    // Verifica que, cuando se pasan valores no nulos al constructor,
    // los getters nunca devuelvan null.
    void getters_noDeberianRetornarNullCuandoSeAsignanValoresNoNull() {
        //  valores válidos para construir el error
        String error = "Error genérico";
        List<String> details = Arrays.asList("detalle 1");

        // construyo la respuesta de error
        ApiErrorResponse response = new ApiErrorResponse(error, details);

        //  me aseguro de que los getters no devuelvan null
        assertNotNull(response.getError());
        assertNotNull(response.getDetails());
    }
}
