package com.urbancollection.ecommerce.api.web.dto;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

// Pruebas unitarias 
// Aquí no probamos lógica de negocio, solo que el objeto se puede crear
// y que sus campos aceptan valores sin lanzar errores.
class BaseProductoRequestTest {

    @Test
    // Verifica que el constructor vacío funcione y devuelva una instancia válida.
    void constructorSinArgumentos_deberiaCrearInstanciaNoNula() {
        BaseProductoRequest request = new BaseProductoRequest();
        assertNotNull(request);
    }

    @Test
    // Recorre todos los campos del DTO usando reflection y trata de asignarles
    // valores de prueba según su tipo. La idea es confirmar que no haya problemas
    // de acceso ni setters raros que lancen excepción.
    void deberiaPermitirAsignarValoresACamposPorReflectionSinErrores() {
        BaseProductoRequest request = new BaseProductoRequest();

        // Recorro todos los atributos declarados en la clase.
        for (Field field : BaseProductoRequest.class.getDeclaredFields()) {
            field.setAccessible(true);

            Object valor = null;
            Class<?> type = field.getType();

            // Asigno un valor de prueba dependiendo del tipo de dato del campo.
            if (type.equals(String.class)) {
                valor = "texto-prueba";
            } else if (type.equals(BigDecimal.class)) {
                valor = BigDecimal.TEN;
            } else if (type.equals(Integer.class) || type.equals(int.class)) {
                valor = 1;
            } else if (type.equals(Long.class) || type.equals(long.class)) {
                valor = 1L;
            }

            // Si pude calcular un valor de prueba, intento asignarlo al campo.
            if (valor != null) {
                Object finalValor = valor;
                assertDoesNotThrow(() -> field.set(request, finalValor),
                        () -> "No debería lanzar excepción al asignar campo: " + field.getName());
            }
        }

        //  verifico que el objeto siga siendo válido.
        assertNotNull(request);
    }
}
