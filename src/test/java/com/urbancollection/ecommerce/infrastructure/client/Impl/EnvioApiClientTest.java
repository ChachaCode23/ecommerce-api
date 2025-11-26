package com.urbancollection.ecommerce.infrastructure.client.Impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

import com.urbancollection.ecommerce.domain.entity.logistica.Envio;
import com.urbancollection.ecommerce.domain.enums.EstadoDeEnvio;
import com.urbancollection.ecommerce.infrastructure.client.Impl.EnvioApiClient;

/**
 * EnvioApiClientTest
 * 
 * Tests unitarios para EnvioApiClient con mocks de RestTemplate.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para EnvioApiClient")
class EnvioApiClientTest {

    @Mock
    private RestTemplate restTemplate;

    private EnvioApiClient envioApiClient;
    private final String baseUrl = "http://localhost:8080";

    @BeforeEach
    void setUp() {
        envioApiClient = new EnvioApiClient(restTemplate, baseUrl);
    }

    @Test
    @DisplayName("listar() debe retornar lista de envíos exitosamente")
    void listar_DebeRetornarListaDeEnvios() {
        // Arrange
        Envio envio1 = crearEnvio(1L, "TRACK001", EstadoDeEnvio.PENDIENTE);
        Envio envio2 = crearEnvio(2L, "TRACK002", EstadoDeEnvio.EN_TRANSITO);
        List<Envio> enviosEsperados = Arrays.asList(envio1, envio2);

        ResponseEntity<List<Envio>> responseEntity = new ResponseEntity<>(enviosEsperados, HttpStatus.OK);

        when(restTemplate.exchange(
            eq(baseUrl + "/api/envios"),
            eq(HttpMethod.GET),
            isNull(),
            any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        // Act
        List<Envio> resultado = envioApiClient.listar();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("TRACK001", resultado.get(0).getTracking());
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
        assertThrows(RuntimeException.class, () -> envioApiClient.listar());
    }

    @Test
    @DisplayName("buscarPorId() debe retornar Optional con envío cuando existe")
    void buscarPorId_DebeRetornarEnvioCuandoExiste() {
        // Arrange
        Long id = 1L;
        Envio envioEsperado = crearEnvio(id, "TRACK123", EstadoDeEnvio.ENTREGADO);
        ResponseEntity<Envio> responseEntity = new ResponseEntity<>(envioEsperado, HttpStatus.OK);

        when(restTemplate.getForEntity(
            eq(baseUrl + "/api/envios/" + id),
            eq(Envio.class)
        )).thenReturn(responseEntity);

        // Act
        Optional<Envio> resultado = envioApiClient.buscarPorId(id);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("TRACK123", resultado.get().getTracking());
    }

    @Test
    @DisplayName("buscarPorId() debe retornar Optional.empty() cuando no existe")
    void buscarPorId_DebeRetornarVacioCuandoNoExiste() {
        // Arrange
        when(restTemplate.getForEntity(anyString(), eq(Envio.class)))
            .thenThrow(new RestClientException("404"));

        // Act
        Optional<Envio> resultado = envioApiClient.buscarPorId(999L);

        // Assert
        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("crear() debe crear envío exitosamente")
    void crear_DebeCrearEnvioExitosamente() {
        // Arrange
        Envio envioNuevo = crearEnvio(null, "TRACK999", EstadoDeEnvio.PENDIENTE);
        Envio envioCreado = crearEnvio(3L, "TRACK999", EstadoDeEnvio.PENDIENTE);
        ResponseEntity<Envio> responseEntity = new ResponseEntity<>(envioCreado, HttpStatus.CREATED);

        when(restTemplate.postForEntity(
            eq(baseUrl + "/api/envios"),
            eq(envioNuevo),
            eq(Envio.class)
        )).thenReturn(responseEntity);

        // Act
        Envio resultado = envioApiClient.crear(envioNuevo);

        // Assert
        assertNotNull(resultado);
        assertEquals(3L, resultado.getId());
        assertEquals("TRACK999", resultado.getTracking());
    }

    @Test
    @DisplayName("crear() debe lanzar RuntimeException cuando hay error")
    void crear_DebeLanzarExcepcionCuandoHayError() {
        // Arrange
        Envio envio = crearEnvio(null, "TRACK001", EstadoDeEnvio.PENDIENTE);
        when(restTemplate.postForEntity(anyString(), any(), eq(Envio.class)))
            .thenThrow(new RestClientException("Error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> envioApiClient.crear(envio));
    }

    @Test
    @DisplayName("actualizar() debe actualizar envío exitosamente")
    void actualizar_DebeActualizarEnvioExitosamente() {
        // Arrange
        Long id = 1L;
        Envio envioCambios = crearEnvio(id, "TRACK001", EstadoDeEnvio.ENTREGADO);
        ResponseEntity<Envio> responseEntity = new ResponseEntity<>(envioCambios, HttpStatus.OK);

        when(restTemplate.exchange(
            eq(baseUrl + "/api/envios/" + id),
            eq(HttpMethod.PUT),
            any(HttpEntity.class),
            eq(Envio.class)
        )).thenReturn(responseEntity);

        // Act
        Envio resultado = envioApiClient.actualizar(id, envioCambios);

        // Assert
        assertNotNull(resultado);
        assertEquals(EstadoDeEnvio.ENTREGADO, resultado.getEstado());
    }

    @Test
    @DisplayName("actualizar() debe lanzar RuntimeException cuando hay error")
    void actualizar_DebeLanzarExcepcionCuandoHayError() {
        // Arrange
        Envio envio = crearEnvio(1L, "TRACK001", EstadoDeEnvio.PENDIENTE);
        when(restTemplate.exchange(
            anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(Envio.class)
        )).thenThrow(new RestClientException("Error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> envioApiClient.actualizar(1L, envio));
    }

    @Test
    @DisplayName("eliminar() debe eliminar envío exitosamente")
    void eliminar_DebeEliminarEnvioExitosamente() {
        // Arrange
        Long id = 1L;
        doNothing().when(restTemplate).delete(baseUrl + "/api/envios/" + id);

        // Act & Assert
        assertDoesNotThrow(() -> envioApiClient.eliminar(id));
        verify(restTemplate, times(1)).delete(anyString());
    }

    @Test
    @DisplayName("eliminar() debe lanzar RuntimeException cuando hay error")
    void eliminar_DebeLanzarExcepcionCuandoHayError() {
        // Arrange
        doThrow(new RestClientException("Error")).when(restTemplate).delete(anyString());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> envioApiClient.eliminar(1L));
    }

    private Envio crearEnvio(Long id, String tracking, EstadoDeEnvio estado) {
        Envio envio = new Envio();
        envio.setId(id);
        envio.setTracking(tracking);
        envio.setEstado(estado);
        return envio;
    }
}