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

import com.urbancollection.ecommerce.domain.entity.ventas.Pedido;
import com.urbancollection.ecommerce.domain.enums.EstadoDePedido;
import com.urbancollection.ecommerce.infrastructure.client.Impl.PedidoApiClient;

/**
 * PedidoApiClientTest
 * 
 * Tests unitarios para PedidoApiClient con mocks de RestTemplate.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para PedidoApiClient")
class PedidoApiClientTest {

    @Mock
    private RestTemplate restTemplate;

    private PedidoApiClient pedidoApiClient;
    private final String baseUrl = "http://localhost:8080";

    @BeforeEach
    void setUp() {
        pedidoApiClient = new PedidoApiClient(restTemplate, baseUrl);
    }

    @Test
    @DisplayName("listar() debe retornar lista de pedidos exitosamente")
    void listar_DebeRetornarListaDePedidos() {
        // Arrange
        Pedido pedido1 = crearPedido(1L, BigDecimal.valueOf(100), EstadoDePedido.PENDIENTE_PAGO);
        Pedido pedido2 = crearPedido(2L, BigDecimal.valueOf(200), EstadoDePedido.PAGADO);
        List<Pedido> pedidosEsperados = Arrays.asList(pedido1, pedido2);

        ResponseEntity<List<Pedido>> responseEntity = new ResponseEntity<>(pedidosEsperados, HttpStatus.OK);

        when(restTemplate.exchange(
            eq(baseUrl + "/api/pedidos"),
            eq(HttpMethod.GET),
            isNull(),
            any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        // Act
        List<Pedido> resultado = pedidoApiClient.listar();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(EstadoDePedido.PENDIENTE_PAGO, resultado.get(0).getEstado());
        verify(restTemplate, times(1)).exchange(
            anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)
        );
    }

    @Test
    @DisplayName("listar() debe lanzar RuntimeException cuando hay error")
    void listar_DebeLanzarExcepcionCuandoHayError() {
        // Arrange
        when(restTemplate.exchange(
            anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)
        )).thenThrow(new RestClientException("Error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> pedidoApiClient.listar());
    }

    @Test
    @DisplayName("buscarPorId() debe retornar Optional con pedido cuando existe")
    void buscarPorId_DebeRetornarPedidoCuandoExiste() {
        // Arrange
        Long id = 1L;
        Pedido pedidoEsperado = crearPedido(id, BigDecimal.valueOf(150), EstadoDePedido.PAGADO);
        ResponseEntity<Pedido> responseEntity = new ResponseEntity<>(pedidoEsperado, HttpStatus.OK);

        when(restTemplate.getForEntity(
            eq(baseUrl + "/api/pedidos/" + id),
            eq(Pedido.class)
        )).thenReturn(responseEntity);

        // Act
        Optional<Pedido> resultado = pedidoApiClient.buscarPorId(id);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(BigDecimal.valueOf(150), resultado.get().getTotal());
    }

    @Test
    @DisplayName("buscarPorId() debe retornar Optional.empty() cuando no existe")
    void buscarPorId_DebeRetornarVacioCuandoNoExiste() {
        // Arrange
        when(restTemplate.getForEntity(anyString(), eq(Pedido.class)))
            .thenThrow(new RestClientException("404"));

        // Act
        Optional<Pedido> resultado = pedidoApiClient.buscarPorId(999L);

        // Assert
        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("crear() debe crear pedido exitosamente")
    void crear_DebeCrearPedidoExitosamente() {
        // Arrange
        Pedido pedidoNuevo = crearPedido(null, BigDecimal.valueOf(300), EstadoDePedido.PENDIENTE_PAGO);
        Pedido pedidoCreado = crearPedido(3L, BigDecimal.valueOf(300), EstadoDePedido.PENDIENTE_PAGO);
        ResponseEntity<Pedido> responseEntity = new ResponseEntity<>(pedidoCreado, HttpStatus.CREATED);

        when(restTemplate.postForEntity(
            eq(baseUrl + "/api/pedidos"),
            eq(pedidoNuevo),
            eq(Pedido.class)
        )).thenReturn(responseEntity);

        // Act
        Pedido resultado = pedidoApiClient.crear(pedidoNuevo);

        // Assert
        assertNotNull(resultado);
        assertEquals(3L, resultado.getId());
    }

    @Test
    @DisplayName("crear() debe lanzar RuntimeException cuando hay error")
    void crear_DebeLanzarExcepcionCuandoHayError() {
        // Arrange
        Pedido pedido = crearPedido(null, BigDecimal.valueOf(100), EstadoDePedido.PENDIENTE_PAGO);
        when(restTemplate.postForEntity(anyString(), any(), eq(Pedido.class)))
            .thenThrow(new RestClientException("Error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> pedidoApiClient.crear(pedido));
    }

    @Test
    @DisplayName("actualizar() debe actualizar pedido exitosamente")
    void actualizar_DebeActualizarPedidoExitosamente() {
        // Arrange
        Long id = 1L;
        Pedido pedidoCambios = crearPedido(id, BigDecimal.valueOf(250), EstadoDePedido.PAGADO);
        ResponseEntity<Pedido> responseEntity = new ResponseEntity<>(pedidoCambios, HttpStatus.OK);

        when(restTemplate.exchange(
            eq(baseUrl + "/api/pedidos/" + id),
            eq(HttpMethod.PUT),
            any(HttpEntity.class),
            eq(Pedido.class)
        )).thenReturn(responseEntity);

        // Act
        Pedido resultado = pedidoApiClient.actualizar(id, pedidoCambios);

        // Assert
        assertNotNull(resultado);
        assertEquals(EstadoDePedido.PAGADO, resultado.getEstado());
    }

    @Test
    @DisplayName("actualizar() debe lanzar RuntimeException cuando hay error")
    void actualizar_DebeLanzarExcepcionCuandoHayError() {
        // Arrange
        Pedido pedido = crearPedido(1L, BigDecimal.valueOf(100), EstadoDePedido.PAGADO);
        when(restTemplate.exchange(
            anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(Pedido.class)
        )).thenThrow(new RestClientException("Error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> pedidoApiClient.actualizar(1L, pedido));
    }

    @Test
    @DisplayName("eliminar() debe eliminar pedido exitosamente")
    void eliminar_DebeEliminarPedidoExitosamente() {
        // Arrange
        Long id = 1L;
        doNothing().when(restTemplate).delete(baseUrl + "/api/pedidos/" + id);

        // Act & Assert
        assertDoesNotThrow(() -> pedidoApiClient.eliminar(id));
        verify(restTemplate, times(1)).delete(anyString());
    }

    @Test
    @DisplayName("eliminar() debe lanzar RuntimeException cuando hay error")
    void eliminar_DebeLanzarExcepcionCuandoHayError() {
        // Arrange
        doThrow(new RestClientException("Error")).when(restTemplate).delete(anyString());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> pedidoApiClient.eliminar(1L));
    }

    private Pedido crearPedido(Long id, BigDecimal total, EstadoDePedido estado) {
        Pedido pedido = new Pedido();
        pedido.setId(id);
        pedido.setTotal(total);
        pedido.setSubtotal(total);
        pedido.setDescuento(BigDecimal.ZERO);
        pedido.setEnvio(BigDecimal.ZERO);
        pedido.setEstado(estado);
        return pedido;
    }
}