package com.urbancollection.ecommerce.infrastructure.client.Impl;

import java.util.List;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.urbancollection.ecommerce.domain.entity.logistica.Envio;
import com.urbancollection.ecommerce.infrastructure.client.IEnvioApiClient;

/**
 * EnvioApiClient
 * 
 * Implementación del cliente HTTP para consumir la API REST de Envíos.
 * Usa RestTemplate para realizar las peticiones HTTP.
 */
public class EnvioApiClient implements IEnvioApiClient {

    // Cliente HTTP de Spring que usamos para llamar a la API REST.
    private final RestTemplate restTemplate;
    // URL base del recurso de envío
    private final String baseUrl;

    // En el constructor recibimos el RestTemplate y la URL base de la API.
    public EnvioApiClient(RestTemplate restTemplate, String baseUrl) {
        this.restTemplate = restTemplate;
        // Concatenamos el path de envíos a la URL base general.
        this.baseUrl = baseUrl + "/api/envios";
    }

    @Override
    public List<Envio> listar() {
        try {
            // Hacemos una llamada GET a /api/envios y esperamos una lista de Envio.
            ResponseEntity<List<Envio>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null, // No hay body en un GET.
                new ParameterizedTypeReference<List<Envio>>() {}
            );
            // Devolvemos el body de la respuesta, que debería ser la lista de envíos.
            return response.getBody();
        } catch (Exception e) {
            // Si algo falla, envolvemos el error en una RuntimeException con mensaje más claro.
            throw new RuntimeException("Error al listar envíos desde la API: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Envio> buscarPorId(Long id) {
        try {
            // GET /api/envios/{id} para obtener un envío específico.
            ResponseEntity<Envio> response = restTemplate.getForEntity(
                baseUrl + "/" + id,
                Envio.class
            );
            // Envolvemos el resultado en Optional por si el body viene null.
            return Optional.ofNullable(response.getBody());
        } catch (Exception e) {
            // Si hay error  devolvemos Optional vacío en lugar de romper.
            return Optional.empty();
        }
    }

    @Override
    public Envio crear(Envio envio) {
        try {
            // POST /api/envios enviando el objeto Envio en el body.
            ResponseEntity<Envio> response = restTemplate.postForEntity(
                baseUrl,
                envio,
                Envio.class
            );
            // Devolvemos el envío que devuelve la API (normalmente con ID ya asignado).
            return response.getBody();
        } catch (Exception e) {
            // Si falla la creación, lanzamos una RuntimeException con el mensaje.
            throw new RuntimeException("Error al crear envío en la API: " + e.getMessage(), e);
        }
    }

    @Override
    public Envio actualizar(Long id, Envio envio) {
        try {
            // Armamos la petición HTTP con el envío en el body.
            HttpEntity<Envio> request = new HttpEntity<>(envio);
            // PUT /api/envios/{id} para actualizar un envío existente.
            ResponseEntity<Envio> response = restTemplate.exchange(
                baseUrl + "/" + id,
                HttpMethod.PUT,
                request,
                Envio.class
            );
            // Devolvemos el envío actualizado que responde la API.
            return response.getBody();
        } catch (Exception e) {
            // Si algo falla, lanzamos una RuntimeException para propagar el error.
            throw new RuntimeException("Error al actualizar envío en la API: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Long id) {
        try {
            // DELETE /api/envios/{id} para borrar un envío por su identificador.
            restTemplate.delete(baseUrl + "/" + id);
        } catch (Exception e) {
            // Si hay error al eliminar, también lo envolvemos en una RuntimeException.
            throw new RuntimeException("Error al eliminar envío en la API: " + e.getMessage(), e);
        }
    }
}
