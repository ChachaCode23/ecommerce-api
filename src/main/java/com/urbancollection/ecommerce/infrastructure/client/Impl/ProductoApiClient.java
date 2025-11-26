package com.urbancollection.ecommerce.infrastructure.client.Impl;

import java.util.List;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.urbancollection.ecommerce.domain.entity.catalogo.Producto;
import com.urbancollection.ecommerce.infrastructure.client.IProductoApiClient;

/**
 * ProductoApiClient
 * 
 * Implementación del cliente HTTP para consumir la API REST de Productos.
 * Usa RestTemplate para realizar las peticiones HTTP.
 * 
 */
public class ProductoApiClient implements IProductoApiClient {

    // Cliente HTTP de Spring que se usa para hacer las llamadas a la API REST.
    private final RestTemplate restTemplate;
    // URL base del recurso de productos
    private final String baseUrl;

    // En el constructor recibimos el RestTemplate ya configurado y la URL base de la API.
    public ProductoApiClient(RestTemplate restTemplate, String baseUrl) {
        this.restTemplate = restTemplate;
        // Aquí concatenamos el path de productos a la URL base general.
        this.baseUrl = baseUrl + "/api/productos";
    }

    @Override
    public List<Producto> listar() {
        try {
            // Llamada GET a /api/productos para obtener la lista completa de productos.
            ResponseEntity<List<Producto>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null, // En un GET no enviamos body.
                new ParameterizedTypeReference<List<Producto>>() {} 
            );
            // Devolvemos el cuerpo de la respuesta que contiene la lista de productos.
            return response.getBody();
        } catch (Exception e) {
            // Si ocurre algún error, lo envolvemos en una RuntimeException con un mensaje más claro.
            throw new RuntimeException("Error al listar productos desde la API: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Producto> buscarPorId(Long id) {
        try {
            // Llamada GET a /api/productos/{id} para traer un producto específico.
            ResponseEntity<Producto> response = restTemplate.getForEntity(
                baseUrl + "/" + id,
                Producto.class
            );
            // Envolvemos el resultado en Optional por si el body viene null.
            return Optional.ofNullable(response.getBody());
        } catch (Exception e) {
            // Si hay error  devolvemos Optional.empty() para no romper el flujo.
            return Optional.empty();
        }
    }

    @Override
    public Producto crear(Producto producto) {
        try {
            // Llamada POST a /api/productos enviando el producto en el body.
            ResponseEntity<Producto> response = restTemplate.postForEntity(
                baseUrl,
                producto,
                Producto.class
            );
            // La API normalmente devuelve el producto creado con el ID ya generado.
            return response.getBody();
        } catch (Exception e) {
            // Si algo falla al crear, lanzamos una RuntimeException con el mensaje de error original.
            throw new RuntimeException("Error al crear producto en la API: " + e.getMessage(), e);
        }
    }

    @Override
    public Producto actualizar(Long id, Producto producto) {
        try {
            // Empaquetamos el producto dentro de un HttpEntity para enviarlo como body del PUT.
            HttpEntity<Producto> request = new HttpEntity<>(producto);
            // Llamada PUT a /api/productos/{id} para actualizar un producto existente.
            ResponseEntity<Producto> response = restTemplate.exchange(
                baseUrl + "/" + id,
                HttpMethod.PUT,
                request,
                Producto.class
            );
            // Devolvemos el producto actualizado que responde la API.
            return response.getBody();
        } catch (Exception e) {
            // Si falla la actualización, lanzamos una RuntimeException para que la capa superior lo maneje.
            throw new RuntimeException("Error al actualizar producto en la API: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Long id) {
        try {
            // Llamada DELETE a /api/productos/{id} para eliminar un producto por su identificador.
            restTemplate.delete(baseUrl + "/" + id);
        } catch (Exception e) {
            // Si ocurre algún error durante el borrado, también lo envolvemos en una RuntimeException.
            throw new RuntimeException("Error al eliminar producto en la API: " + e.getMessage(), e);
        }
    }
}
