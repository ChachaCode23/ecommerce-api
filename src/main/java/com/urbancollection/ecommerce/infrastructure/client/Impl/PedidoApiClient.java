package com.urbancollection.ecommerce.infrastructure.client.Impl;

import java.util.List;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.urbancollection.ecommerce.domain.entity.ventas.Pedido;
import com.urbancollection.ecommerce.infrastructure.client.IPedidoApiClient;

/**
 * PedidoApiClient
 * 
 * Implementación del cliente HTTP para consumir la API REST de Pedidos.
 * Usa RestTemplate para realizar las peticiones HTTP.
 */
public class PedidoApiClient implements IPedidoApiClient {

    // Cliente HTTP de Spring que usamos para llamar a la API REST
    private final RestTemplate restTemplate;
    // URL base del recurso de pedidos 
    private final String baseUrl;

    // El constructor recibe el RestTemplate ya configurado y la URL base de la API
    public PedidoApiClient(RestTemplate restTemplate, String baseUrl) {
        this.restTemplate = restTemplate;
        // Aquí se agrega el path de pedidos a la URL base general
        this.baseUrl = baseUrl + "/api/pedidos";
    }

    @Override
    public List<Pedido> listar() {
        try {
            // Llamada GET a /api/pedidos que devuelve una lista de Pedido
            ResponseEntity<List<Pedido>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null, // No enviamos body en un GET
                new ParameterizedTypeReference<List<Pedido>>() {}
            );
            // Retornamos el body de la respuesta (la lista de pedidos)
            return response.getBody();
        } catch (Exception e) {
            // Si algo falla, lanzamos una RuntimeException con un mensaje más claro
            throw new RuntimeException("Error al listar pedidos desde la API: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Pedido> buscarPorId(Long id) {
        try {
            // Llamada GET a /api/pedidos/{id} para obtener un pedido específico
            ResponseEntity<Pedido> response = restTemplate.getForEntity(
                baseUrl + "/" + id,
                Pedido.class
            );
            // Envolvemos la respuesta en Optional por si viene null
            return Optional.ofNullable(response.getBody());
        } catch (Exception e) {
            // Si hay error (404, conexión, etc.) devolvemos Optional vacío
            return Optional.empty();
        }
    }

    @Override
    public Pedido crear(Pedido pedido) {
        try {
            // Llamada POST a /api/pedidos enviando el pedido en el body
            ResponseEntity<Pedido> response = restTemplate.postForEntity(
                baseUrl,
                pedido,
                Pedido.class
            );
            // La API debería devolver el pedido creado (normalmente con ID generado)
            return response.getBody();
        } catch (Exception e) {
            // Propagamos el error envuelto en una RuntimeException
            throw new RuntimeException("Error al crear pedido en la API: " + e.getMessage(), e);
        }
    }

    @Override
    public Pedido actualizar(Long id, Pedido pedido) {
        try {
            // Armamos la entidad HTTP con el pedido como body
            HttpEntity<Pedido> request = new HttpEntity<>(pedido);
            // Llamada PUT a /api/pedidos/{id} para actualizar un pedido existente
            ResponseEntity<Pedido> response = restTemplate.exchange(
                baseUrl + "/" + id,
                HttpMethod.PUT,
                request,
                Pedido.class
            );
            // Retornamos el pedido actualizado que responde la API
            return response.getBody();
        } catch (Exception e) {
            // Si algo sale mal, lanzamos una RuntimeException con el mensaje de error
            throw new RuntimeException("Error al actualizar pedido en la API: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Long id) {
        try {
            // Llamada DELETE a /api/pedidos/{id} para eliminar un pedido
            restTemplate.delete(baseUrl + "/" + id);
        } catch (Exception e) {
            // Si falla el borrado, también lanzamos RuntimeException
            throw new RuntimeException("Error al eliminar pedido en la API: " + e.getMessage(), e);
        }
    }
}
