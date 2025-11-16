package com.urbancollection.ecommerce.api.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Manejo global de errores para la API REST.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /**
     * 400 - Errores de validación (@Valid)
     * Devuelve JSON con estructura:
     * {
     *   "error": "Solicitud inválida",
     *   "details": [
     *      "campo: mensaje"
     *   ]
     * }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {

        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .toList();

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "Solicitud inválida");
        body.put("details", details);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    private String formatFieldError(FieldError err) {
        // items[0].cantidad: La cantidad debe ser mayor que 0
        return err.getField() + ": " + err.getDefaultMessage();
    }
}
