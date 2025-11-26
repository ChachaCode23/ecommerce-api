package com.urbancollection.ecommerce.infrastructure.client.Impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.urbancollection.ecommerce.domain.entity.catalogo.Producto;
import com.urbancollection.ecommerce.infrastructure.client.Impl.ProductoApiClient;

/**
 * ProductoApiClientTest
 * 
 * Tests unitarios para ProductoApiClient.
 * Verifica que el cliente HTTP funciona correctamente usando mocks de RestTemplate.
 * 
 * Cobertura:
 * - listar(): GET /api/productos
 * - buscarPorId(): GET /api/productos/{id}
 * - crear(): POST /api/productos
 * - actualizar(): PUT /api/productos/{id}
 * - eliminar(): DELETE /api/productos/{id}
 * - Manejo de errores HTTP
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para ProductoApiClient")
class ProductoApiClientTest {

    @Mock
    private RestTemplate restTemplate;

    private ProductoApiClient productoApiClient;
    private final String baseUrl = "http://localhost:8080";

    @BeforeEach
    void setUp() {
        productoApiClient = new ProductoApiClient(restTemplate, baseUrl);
    }

    // =========================
    // TESTS LISTAR
    // =========================

    @Test
    @DisplayName("listar() debe retornar lista de productos exitosamente")
    void listar_DebeRetornarListaDeProductos() {
        // Arrange
        Producto producto1 = crearProducto(1L, "Producto 1", BigDecimal.valueOf(100));
        Producto producto2 = crearProducto(2L, "Producto 2", BigDecimal.valueOf(200));
        List<Producto> productosEsperados = Arrays.asList(producto1, producto2);

        ResponseEntity<List<Producto>> responseEntity = new ResponseEntity<>(productosEsperados, HttpStatus.OK);

        when(restTemplate.exchange(
            eq(baseUrl + "/api/productos"),
            eq(HttpMethod.GET),
            isNull(),
            any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        // Act
        List<Producto> resultado = productoApiClient.listar();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Producto 1", resultado.get(0).getNombre());
        assertEquals("Producto 2", resultado.get(1).getNombre());

        verify(restTemplate, times(1)).exchange(
            eq(baseUrl + "/api/productos"),
            eq(HttpMethod.GET),
            isNull(),
            any(ParameterizedTypeReference.class)
        );
    }

    @Test
    @DisplayName("listar() debe lanzar RuntimeException cuando hay error HTTP")
    void listar_DebeLanzarExcepcionCuandoHayError() {
        // Arrange
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            isNull(),
            any(ParameterizedTypeReference.class)
        )).thenThrow(new RestClientException("Error de conexión"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productoApiClient.listar();
        });

        assertTrue(exception.getMessage().contains("Error al listar productos desde la API"));
    }

    // =========================
    // TESTS BUSCAR POR ID
    // =========================

    @Test
    @DisplayName("buscarPorId() debe retornar Optional con producto cuando existe")
    void buscarPorId_DebeRetornarProductoCuandoExiste() {
        // Arrange
        Long id = 1L;
        Producto productoEsperado = crearProducto(id, "Producto Test", BigDecimal.valueOf(150));
        ResponseEntity<Producto> responseEntity = new ResponseEntity<>(productoEsperado, HttpStatus.OK);

        when(restTemplate.getForEntity(
            eq(baseUrl + "/api/productos/" + id),
            eq(Producto.class)
        )).thenReturn(responseEntity);

        // Act
        Optional<Producto> resultado = productoApiClient.buscarPorId(id);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("Producto Test", resultado.get().getNombre());
        assertEquals(id, resultado.get().getId());

        verify(restTemplate, times(1)).getForEntity(
            eq(baseUrl + "/api/productos/" + id),
            eq(Producto.class)
        );
    }

    @Test
    @DisplayName("buscarPorId() debe retornar Optional.empty() cuando no existe")
    void buscarPorId_DebeRetornarVacioCuandoNoExiste() {
        // Arrange
        Long id = 999L;
        when(restTemplate.getForEntity(
            anyString(),
            eq(Producto.class)
        )).thenThrow(new RestClientException("404 Not Found"));

        // Act
        Optional<Producto> resultado = productoApiClient.buscarPorId(id);

        // Assert
        assertFalse(resultado.isPresent());
    }

    // =========================
    // TESTS CREAR
    // =========================

    @Test
    @DisplayName("crear() debe crear producto exitosamente")
    void crear_DebeCrearProductoExitosamente() {
        // Arrange
        Producto productoNuevo = crearProducto(null, "Producto Nuevo", BigDecimal.valueOf(300));
        Producto productoCreado = crearProducto(3L, "Producto Nuevo", BigDecimal.valueOf(300));
        ResponseEntity<Producto> responseEntity = new ResponseEntity<>(productoCreado, HttpStatus.CREATED);

        when(restTemplate.postForEntity(
            eq(baseUrl + "/api/productos"),
            eq(productoNuevo),
            eq(Producto.class)
        )).thenReturn(responseEntity);

        // Act
        Producto resultado = productoApiClient.crear(productoNuevo);

        // Assert
        assertNotNull(resultado);
        assertEquals(3L, resultado.getId());
        assertEquals("Producto Nuevo", resultado.getNombre());

        verify(restTemplate, times(1)).postForEntity(
            eq(baseUrl + "/api/productos"),
            eq(productoNuevo),
            eq(Producto.class)
        );
    }

    @Test
    @DisplayName("crear() debe lanzar RuntimeException cuando hay error")
    void crear_DebeLanzarExcepcionCuandoHayError() {
        // Arrange
        Producto producto = crearProducto(null, "Producto", BigDecimal.valueOf(100));
        when(restTemplate.postForEntity(
            anyString(),
            any(),
            eq(Producto.class)
        )).thenThrow(new RestClientException("Error de validación"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productoApiClient.crear(producto);
        });

        assertTrue(exception.getMessage().contains("Error al crear producto en la API"));
    }

    // =========================
    // TESTS ACTUALIZAR
    // =========================

    @Test
    @DisplayName("actualizar() debe actualizar producto exitosamente")
    void actualizar_DebeActualizarProductoExitosamente() {
        // Arrange
        Long id = 1L;
        Producto productoCambios = crearProducto(id, "Producto Actualizado", BigDecimal.valueOf(250));
        ResponseEntity<Producto> responseEntity = new ResponseEntity<>(productoCambios, HttpStatus.OK);

        when(restTemplate.exchange(
            eq(baseUrl + "/api/productos/" + id),
            eq(HttpMethod.PUT),
            any(HttpEntity.class),
            eq(Producto.class)
        )).thenReturn(responseEntity);

        // Act
        Producto resultado = productoApiClient.actualizar(id, productoCambios);

        // Assert
        assertNotNull(resultado);
        assertEquals("Producto Actualizado", resultado.getNombre());
        assertEquals(BigDecimal.valueOf(250), resultado.getPrecio());

        verify(restTemplate, times(1)).exchange(
            eq(baseUrl + "/api/productos/" + id),
            eq(HttpMethod.PUT),
            any(HttpEntity.class),
            eq(Producto.class)
        );
    }

    @Test
    @DisplayName("actualizar() debe lanzar RuntimeException cuando hay error")
    void actualizar_DebeLanzarExcepcionCuandoHayError() {
        // Arrange
        Long id = 1L;
        Producto producto = crearProducto(id, "Producto", BigDecimal.valueOf(100));
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.PUT),
            any(HttpEntity.class),
            eq(Producto.class)
        )).thenThrow(new RestClientException("Error de red"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productoApiClient.actualizar(id, producto);
        });

        assertTrue(exception.getMessage().contains("Error al actualizar producto en la API"));
    }

    // =========================
    // TESTS ELIMINAR
    // =========================

    @Test
    @DisplayName("eliminar() debe eliminar producto exitosamente")
    void eliminar_DebeEliminarProductoExitosamente() {
        // Arrange
        Long id = 1L;
        doNothing().when(restTemplate).delete(baseUrl + "/api/productos/" + id);

        // Act
        assertDoesNotThrow(() -> productoApiClient.eliminar(id));

        // Assert
        verify(restTemplate, times(1)).delete(baseUrl + "/api/productos/" + id);
    }

    @Test
    @DisplayName("eliminar() debe lanzar RuntimeException cuando hay error")
    void eliminar_DebeLanzarExcepcionCuandoHayError() {
        // Arrange
        Long id = 1L;
        doThrow(new RestClientException("Producto tiene referencias"))
            .when(restTemplate).delete(anyString());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productoApiClient.eliminar(id);
        });

        assertTrue(exception.getMessage().contains("Error al eliminar producto en la API"));
    }

    // =========================
    // MÉTODOS AUXILIARES
    // =========================

    private Producto crearProducto(Long id, String nombre, BigDecimal precio) {
        Producto producto = new Producto();
        producto.setId(id);
        producto.setNombre(nombre);
        producto.setPrecio(precio);
        producto.setSku("SKU-" + id);
        producto.setStock(10);
        producto.setActivo(true);
        return producto;
    }
}