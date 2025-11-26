package com.urbancollection.ecommerce.infrastructure.client.Impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

import com.urbancollection.ecommerce.domain.entity.catalogo.Cupon;
import com.urbancollection.ecommerce.domain.enums.TipoDescuento;
import com.urbancollection.ecommerce.infrastructure.client.Impl.CuponApiClient;

/**
 * CuponApiClientTest
 * 
 * Tests unitarios para CuponApiClient con mocks de RestTemplate.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para CuponApiClient")
class CuponApiClientTest {

    @Mock
    private RestTemplate restTemplate;

    private CuponApiClient cuponApiClient;
    private final String baseUrl = "http://localhost:8080";

    @BeforeEach
    void setUp() {
        cuponApiClient = new CuponApiClient(restTemplate, baseUrl);
    }

    @Test
    @DisplayName("listar() debe retornar lista de cupones exitosamente")
    void listar_DebeRetornarListaDeCupones() {
        // Arrange
        Cupon cupon1 = crearCupon(1L, "BLACK25", TipoDescuento.PORCENTAJE, BigDecimal.valueOf(25));
        Cupon cupon2 = crearCupon(2L, "SAVE50", TipoDescuento.MONTO_FIJO, BigDecimal.valueOf(50));
        List<Cupon> cuponesEsperados = Arrays.asList(cupon1, cupon2);

        ResponseEntity<List<Cupon>> responseEntity = new ResponseEntity<>(cuponesEsperados, HttpStatus.OK);

        when(restTemplate.exchange(
            eq(baseUrl + "/api/cupones"),
            eq(HttpMethod.GET),
            isNull(),
            any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        // Act
        List<Cupon> resultado = cuponApiClient.listar();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("BLACK25", resultado.get(0).getCodigo());
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
        assertThrows(RuntimeException.class, () -> cuponApiClient.listar());
    }

    @Test
    @DisplayName("buscarPorId() debe retornar Optional con cupón cuando existe")
    void buscarPorId_DebeRetornarCuponCuandoExiste() {
        // Arrange
        Long id = 1L;
        Cupon cuponEsperado = crearCupon(id, "SUMMER30", TipoDescuento.PORCENTAJE, BigDecimal.valueOf(30));
        ResponseEntity<Cupon> responseEntity = new ResponseEntity<>(cuponEsperado, HttpStatus.OK);

        when(restTemplate.getForEntity(
            eq(baseUrl + "/api/cupones/" + id),
            eq(Cupon.class)
        )).thenReturn(responseEntity);

        // Act
        Optional<Cupon> resultado = cuponApiClient.buscarPorId(id);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("SUMMER30", resultado.get().getCodigo());
    }

    @Test
    @DisplayName("buscarPorId() debe retornar Optional.empty() cuando no existe")
    void buscarPorId_DebeRetornarVacioCuandoNoExiste() {
        // Arrange
        when(restTemplate.getForEntity(anyString(), eq(Cupon.class)))
            .thenThrow(new RestClientException("404"));

        // Act
        Optional<Cupon> resultado = cuponApiClient.buscarPorId(999L);

        // Assert
        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("crear() debe crear cupón exitosamente")
    void crear_DebeCrearCuponExitosamente() {
        // Arrange
        Cupon cuponNuevo = crearCupon(null, "NEWDEAL", TipoDescuento.PORCENTAJE, BigDecimal.valueOf(15));
        Cupon cuponCreado = crearCupon(3L, "NEWDEAL", TipoDescuento.PORCENTAJE, BigDecimal.valueOf(15));
        ResponseEntity<Cupon> responseEntity = new ResponseEntity<>(cuponCreado, HttpStatus.CREATED);

        when(restTemplate.postForEntity(
            eq(baseUrl + "/api/cupones"),
            eq(cuponNuevo),
            eq(Cupon.class)
        )).thenReturn(responseEntity);

        // Act
        Cupon resultado = cuponApiClient.crear(cuponNuevo);

        // Assert
        assertNotNull(resultado);
        assertEquals(3L, resultado.getId());
        assertEquals("NEWDEAL", resultado.getCodigo());
    }

    @Test
    @DisplayName("crear() debe lanzar RuntimeException cuando hay error")
    void crear_DebeLanzarExcepcionCuandoHayError() {
        // Arrange
        Cupon cupon = crearCupon(null, "ERROR", TipoDescuento.PORCENTAJE, BigDecimal.valueOf(10));
        when(restTemplate.postForEntity(anyString(), any(), eq(Cupon.class)))
            .thenThrow(new RestClientException("Error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> cuponApiClient.crear(cupon));
    }

    @Test
    @DisplayName("actualizar() debe actualizar cupón exitosamente")
    void actualizar_DebeActualizarCuponExitosamente() {
        // Arrange
        Long id = 1L;
        Cupon cuponCambios = crearCupon(id, "UPDATED50", TipoDescuento.MONTO_FIJO, BigDecimal.valueOf(50));
        ResponseEntity<Cupon> responseEntity = new ResponseEntity<>(cuponCambios, HttpStatus.OK);

        when(restTemplate.exchange(
            eq(baseUrl + "/api/cupones/" + id),
            eq(HttpMethod.PUT),
            any(HttpEntity.class),
            eq(Cupon.class)
        )).thenReturn(responseEntity);

        // Act
        Cupon resultado = cuponApiClient.actualizar(id, cuponCambios);

        // Assert
        assertNotNull(resultado);
        assertEquals("UPDATED50", resultado.getCodigo());
        assertEquals(TipoDescuento.MONTO_FIJO, resultado.getTipo());
    }

    @Test
    @DisplayName("actualizar() debe lanzar RuntimeException cuando hay error")
    void actualizar_DebeLanzarExcepcionCuandoHayError() {
        // Arrange
        Cupon cupon = crearCupon(1L, "TEST", TipoDescuento.PORCENTAJE, BigDecimal.valueOf(10));
        when(restTemplate.exchange(
            anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(Cupon.class)
        )).thenThrow(new RestClientException("Error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> cuponApiClient.actualizar(1L, cupon));
    }

    @Test
    @DisplayName("eliminar() debe eliminar cupón exitosamente")
    void eliminar_DebeEliminarCuponExitosamente() {
        // Arrange
        Long id = 1L;
        doNothing().when(restTemplate).delete(baseUrl + "/api/cupones/" + id);

        // Act & Assert
        assertDoesNotThrow(() -> cuponApiClient.eliminar(id));
        verify(restTemplate, times(1)).delete(anyString());
    }

    @Test
    @DisplayName("eliminar() debe lanzar RuntimeException cuando hay error")
    void eliminar_DebeLanzarExcepcionCuandoHayError() {
        // Arrange
        doThrow(new RestClientException("Error")).when(restTemplate).delete(anyString());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> cuponApiClient.eliminar(1L));
    }

    private Cupon crearCupon(Long id, String codigo, TipoDescuento tipo, BigDecimal valorDescuento) {
        Cupon cupon = new Cupon();
        cupon.setId(id);
        cupon.setCodigo(codigo);
        cupon.setActivo(true);
        cupon.setTipo(tipo);
        cupon.setValorDescuento(valorDescuento);
        cupon.setFechaInicio(LocalDateTime.now());
        cupon.setFechaFin(LocalDateTime.now().plusDays(30));
        return cupon;
    }
}