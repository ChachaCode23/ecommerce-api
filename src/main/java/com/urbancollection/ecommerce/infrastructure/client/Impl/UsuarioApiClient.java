package com.urbancollection.ecommerce.infrastructure.client.Impl;

import java.util.List;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.urbancollection.ecommerce.domain.entity.usuarios.Usuario;
import com.urbancollection.ecommerce.infrastructure.client.IUsuarioApiClient;

/**
 * UsuarioApiClient
 * 
 * Implementación del cliente HTTP para consumir la API REST de Usuarios.
 * Usa RestTemplate para realizar las peticiones HTTP.
 */
public class UsuarioApiClient implements IUsuarioApiClient {

    // RestTemplate es el cliente HTTP de Spring que usamos para llamar a la API.
    private final RestTemplate restTemplate;
    // baseUrl almacena la URL base del recurso de usuarios
    private final String baseUrl;

    // En el constructor recibimos el RestTemplate y la URL base general de la API.
    public UsuarioApiClient(RestTemplate restTemplate, String baseUrl) {
        this.restTemplate = restTemplate;
        // Aquí concatenamos el path de usuarios a la URL base.
        this.baseUrl = baseUrl + "/api/usuarios";
    }

    @Override
    public List<Usuario> listar() {
        try {
            // Llamada GET a /api/usuarios, esperamos una lista de Usuario como respuesta.
            ResponseEntity<List<Usuario>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null, // No enviamos body en este GET.
                new ParameterizedTypeReference<List<Usuario>>() {}
            );
            // Devolvemos el body de la respuesta, que debería ser la lista de usuarios.
            return response.getBody();
        } catch (Exception e) {
            // Si ocurre cualquier error, lanzamos una RuntimeException más descriptiva.
            throw new RuntimeException("Error al listar usuarios desde la API: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        try {
            // Llamada GET a /api/usuarios/{id} para traer un usuario específico.
            ResponseEntity<Usuario> response = restTemplate.getForEntity(
                baseUrl + "/" + id,
                Usuario.class
            );
            // Envolvemos el resultado en Optional por si el body viene null.
            return Optional.ofNullable(response.getBody());
        } catch (Exception e) {
            // En caso de error, devolvemos Optional vacío.
            return Optional.empty();
        }
    }

    @Override
    public Usuario crear(Usuario usuario) {
        try {
            // Llamada POST a /api/usuarios enviando el Usuario en el body.
            ResponseEntity<Usuario> response = restTemplate.postForEntity(
                baseUrl,
                usuario,
                Usuario.class
            );
            // La API debería devolver el usuario creado, normalmente con su ID generado.
            return response.getBody();
        } catch (Exception e) {
            // Si falla la creación, propagamos el error usando RuntimeException.
            throw new RuntimeException("Error al crear usuario en la API: " + e.getMessage(), e);
        }
    }

    @Override
    public Usuario actualizar(Long id, Usuario usuario) {
        try {
            // HttpEntity envuelve el usuario para enviarlo como body en la petición PUT.
            HttpEntity<Usuario> request = new HttpEntity<>(usuario);
            // Llamada PUT a /api/usuarios/{id} para actualizar un usuario ya existente.
            ResponseEntity<Usuario> response = restTemplate.exchange(
                baseUrl + "/" + id,
                HttpMethod.PUT,
                request,
                Usuario.class
            );
            // Devolvemos el usuario actualizado que responde la API.
            return response.getBody();
        } catch (Exception e) {
            // Si algo falla en la actualización, lanzamos RuntimeException con el detalle del error.
            throw new RuntimeException("Error al actualizar usuario en la API: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Long id) {
        try {
            // Llamada DELETE a /api/usuarios/{id} para eliminar el usuario.
            restTemplate.delete(baseUrl + "/" + id);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar usuario en la API: " + e.getMessage(), e);
        }
    }
}
