package com.urbancollection.ecommerce.infrastructure.client.Impl;

import java.util.List;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.urbancollection.ecommerce.domain.entity.catalogo.Cupon;
import com.urbancollection.ecommerce.infrastructure.client.ICuponApiClient;

/**
 * CuponApiClient
 * 
 * Implementación del cliente HTTP para consumir la API REST de Cupones.
 * Usa RestTemplate para realizar las peticiones HTTP.
 */
public class CuponApiClient implements ICuponApiClient {

    // Cliente HTTP de Spring que se encarga de hacer las llamadas REST.
    private final RestTemplate restTemplate;
    // URL base del servicio de cupones 
    private final String baseUrl;

    // En el constructor recibimos el RestTemplate configurado y la url base de la API.
    public CuponApiClient(RestTemplate restTemplate, String baseUrl) {
        this.restTemplate = restTemplate;
        // Aquí concatenamos el path de cupones a la URL base general.
        this.baseUrl = baseUrl + "/api/cupones";
    }

    @Override
    public List<Cupon> listar() {
        try {
            // Usamos exchange porque queremos recibir una lista tipada de Cupon.
            ResponseEntity<List<Cupon>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null, // No enviamos body en un GET.
                new ParameterizedTypeReference<List<Cupon>>() {}
            );
            // Devolvemos el body de la respuesta que debería ser la lista de cupones.
            return response.getBody();
        } catch (Exception e) {
            // Si falla la llamada, lanzamos una RuntimeException con un mensaje más claro.
            throw new RuntimeException("Error al listar cupones desde la API: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Cupon> buscarPorId(Long id) {
        try {
            // Llamada GET a /api/cupones/{id} para buscar un cupón específico.
            ResponseEntity<Cupon> response = restTemplate.getForEntity(
                baseUrl + "/" + id,
                Cupon.class
            );
            // Devolvemos el cupón envuelto en Optional, por si viene null.
            return Optional.ofNullable(response.getBody());
        } catch (Exception e) {
            // Si algo sale mal (404, error de red, etc.), devolvemos Optional vacío.
            return Optional.empty();
        }
    }

    @Override
    public Cupon crear(Cupon cupon) {
        try {
            // Enviamos un POST a la API con el cupón en el cuerpo de la petición.
            ResponseEntity<Cupon> response = restTemplate.postForEntity(
                baseUrl,
                cupon,
                Cupon.class
            );
            // Devolvemos el cupón que responde la API (normalmente con ID ya asignado).
            return response.getBody();
        } catch (Exception e) {
            // Si falla la creación, lanzamos una RuntimeException con el detalle del error.
            throw new RuntimeException("Error al crear cupón en la API: " + e.getMessage(), e);
        }
    }

    @Override
    public Cupon actualizar(Long id, Cupon cupon) {
        try {
            // Creamos un HttpEntity para envolver el cupón como body del PUT.
            HttpEntity<Cupon> request = new HttpEntity<>(cupon);
            // Usamos exchange para llamar a PUT /api/cupones/{id}.
            ResponseEntity<Cupon> response = restTemplate.exchange(
                baseUrl + "/" + id,
                HttpMethod.PUT,
                request,
                Cupon.class
            );
            // Devolvemos el cupón actualizado que envía la API.
            return response.getBody();
        } catch (Exception e) {
            // Si falla la actualización, lanzamos una RuntimeException con mensaje.
            throw new RuntimeException("Error al actualizar cupón en la API: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Long id) {
        try {
            // Llamada DELETE a /api/cupones/{id} para borrar el cupón.
            restTemplate.delete(baseUrl + "/" + id);
        } catch (Exception e) {
            // Si hay error al eliminar, lo envolvemos en una RuntimeException.
            throw new RuntimeException("Error al eliminar cupón en la API: " + e.getMessage(), e);
        }
    }
}
