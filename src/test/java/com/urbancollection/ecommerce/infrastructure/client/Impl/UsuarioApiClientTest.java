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

import com.urbancollection.ecommerce.domain.entity.usuarios.Usuario;
import com.urbancollection.ecommerce.infrastructure.client.Impl.UsuarioApiClient;

/**
 * UsuarioApiClientTest
 * 
 * Tests unitarios para UsuarioApiClient con mocks de RestTemplate.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para UsuarioApiClient")
class UsuarioApiClientTest {

    @Mock
    private RestTemplate restTemplate;

    private UsuarioApiClient usuarioApiClient;
    private final String baseUrl = "http://localhost:8080";

    @BeforeEach
    void setUp() {
        usuarioApiClient = new UsuarioApiClient(restTemplate, baseUrl);
    }

    @Test
    @DisplayName("listar() debe retornar lista de usuarios exitosamente")
    void listar_DebeRetornarListaDeUsuarios() {
        // Arrange
        Usuario usuario1 = crearUsuario(1L, "Juan", "juan@test.com");
        Usuario usuario2 = crearUsuario(2L, "Maria", "maria@test.com");
        List<Usuario> usuariosEsperados = Arrays.asList(usuario1, usuario2);

        ResponseEntity<List<Usuario>> responseEntity = new ResponseEntity<>(usuariosEsperados, HttpStatus.OK);

        when(restTemplate.exchange(
            eq(baseUrl + "/api/usuarios"),
            eq(HttpMethod.GET),
            isNull(),
            any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        // Act
        List<Usuario> resultado = usuarioApiClient.listar();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Juan", resultado.get(0).getNombre());
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
        assertThrows(RuntimeException.class, () -> usuarioApiClient.listar());
    }

    @Test
    @DisplayName("buscarPorId() debe retornar Optional con usuario cuando existe")
    void buscarPorId_DebeRetornarUsuarioCuandoExiste() {
        // Arrange
        Long id = 1L;
        Usuario usuarioEsperado = crearUsuario(id, "Juan", "juan@test.com");
        ResponseEntity<Usuario> responseEntity = new ResponseEntity<>(usuarioEsperado, HttpStatus.OK);

        when(restTemplate.getForEntity(
            eq(baseUrl + "/api/usuarios/" + id),
            eq(Usuario.class)
        )).thenReturn(responseEntity);

        // Act
        Optional<Usuario> resultado = usuarioApiClient.buscarPorId(id);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("Juan", resultado.get().getNombre());
    }

    @Test
    @DisplayName("buscarPorId() debe retornar Optional.empty() cuando no existe")
    void buscarPorId_DebeRetornarVacioCuandoNoExiste() {
        // Arrange
        when(restTemplate.getForEntity(anyString(), eq(Usuario.class)))
            .thenThrow(new RestClientException("404"));

        // Act
        Optional<Usuario> resultado = usuarioApiClient.buscarPorId(999L);

        // Assert
        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("crear() debe crear usuario exitosamente")
    void crear_DebeCrearUsuarioExitosamente() {
        // Arrange
        Usuario usuarioNuevo = crearUsuario(null, "Pedro", "pedro@test.com");
        Usuario usuarioCreado = crearUsuario(3L, "Pedro", "pedro@test.com");
        ResponseEntity<Usuario> responseEntity = new ResponseEntity<>(usuarioCreado, HttpStatus.CREATED);

        when(restTemplate.postForEntity(
            eq(baseUrl + "/api/usuarios"),
            eq(usuarioNuevo),
            eq(Usuario.class)
        )).thenReturn(responseEntity);

        // Act
        Usuario resultado = usuarioApiClient.crear(usuarioNuevo);

        // Assert
        assertNotNull(resultado);
        assertEquals(3L, resultado.getId());
        assertEquals("Pedro", resultado.getNombre());
    }

    @Test
    @DisplayName("crear() debe lanzar RuntimeException cuando hay error")
    void crear_DebeLanzarExcepcionCuandoHayError() {
        // Arrange
        Usuario usuario = crearUsuario(null, "Test", "test@test.com");
        when(restTemplate.postForEntity(anyString(), any(), eq(Usuario.class)))
            .thenThrow(new RestClientException("Error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> usuarioApiClient.crear(usuario));
    }

    @Test
    @DisplayName("actualizar() debe actualizar usuario exitosamente")
    void actualizar_DebeActualizarUsuarioExitosamente() {
        // Arrange
        Long id = 1L;
        Usuario usuarioCambios = crearUsuario(id, "Juan Actualizado", "juan@test.com");
        ResponseEntity<Usuario> responseEntity = new ResponseEntity<>(usuarioCambios, HttpStatus.OK);

        when(restTemplate.exchange(
            eq(baseUrl + "/api/usuarios/" + id),
            eq(HttpMethod.PUT),
            any(HttpEntity.class),
            eq(Usuario.class)
        )).thenReturn(responseEntity);

        // Act
        Usuario resultado = usuarioApiClient.actualizar(id, usuarioCambios);

        // Assert
        assertNotNull(resultado);
        assertEquals("Juan Actualizado", resultado.getNombre());
    }

    @Test
    @DisplayName("actualizar() debe lanzar RuntimeException cuando hay error")
    void actualizar_DebeLanzarExcepcionCuandoHayError() {
        // Arrange
        Usuario usuario = crearUsuario(1L, "Test", "test@test.com");
        when(restTemplate.exchange(
            anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(Usuario.class)
        )).thenThrow(new RestClientException("Error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> usuarioApiClient.actualizar(1L, usuario));
    }

    @Test
    @DisplayName("eliminar() debe eliminar usuario exitosamente")
    void eliminar_DebeEliminarUsuarioExitosamente() {
        // Arrange
        Long id = 1L;
        doNothing().when(restTemplate).delete(baseUrl + "/api/usuarios/" + id);

        // Act & Assert
        assertDoesNotThrow(() -> usuarioApiClient.eliminar(id));
        verify(restTemplate, times(1)).delete(anyString());
    }

    @Test
    @DisplayName("eliminar() debe lanzar RuntimeException cuando hay error")
    void eliminar_DebeLanzarExcepcionCuandoHayError() {
        // Arrange
        doThrow(new RestClientException("Error")).when(restTemplate).delete(anyString());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> usuarioApiClient.eliminar(1L));
    }

    private Usuario crearUsuario(Long id, String nombre, String correo) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        usuario.setRol("CUSTOMER");
        return usuario;
    }
}