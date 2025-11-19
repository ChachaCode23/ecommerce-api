package com.urbancollection.ecommerce.api.web.dto;

import java.util.List;

// Clase que uso para enviar errores estructurados en las respuestas de la API.
// Normalmente se convierte a JSON y el cliente lo recibe con el mensaje de error.
public class ApiErrorResponse {

    // Mensaje principal del error
    private String error;
    // Lista con más detalles del error, por ejemplo, mensajes por cada campo inválido.
    private List<String> details;

    // Constructor donde se asigna el mensaje principal y la lista de detalles.
    public ApiErrorResponse(String error, List<String> details) {
        this.error = error;
        this.details = details;
    }

    // Devuelve el mensaje principal del error.
    public String getError() {
        return error;
    }

    // Devuelve la lista de detalles del error.
    public List<String> getDetails() {
        return details;
    }
}
