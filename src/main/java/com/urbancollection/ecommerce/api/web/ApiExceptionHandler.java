package com.urbancollection.ecommerce.api.web;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * Clase de consejo global (@RestControllerAdvice) para manejar excepciones
 * que ocurren en los controladores REST y devolver respuestas claras al cliente.
 *
 * aqui centralizamos el manejo de errores en un solo lugar.
 */
public class ApiExceptionHandler {

  /**
   * Maneja errores de validación de argumentos (Bean Validation) cuando
   * un @RequestBody o @ModelAttribute no cumple las anotaciones (@NotNull, @Size, etc.).
   *
   * Ejemplo:
   * - Si el DTO tiene un campo @NotBlank y llega vacío, cae aquí con MethodArgumentNotValidException.
   *
   * Respuesta:
   * - HTTP 400 (BAD_REQUEST)
   * - JSON con:
   *   {
   *     "error": "Datos invalidos",
   *     "fields": { "campo1": "mensaje de error", "campo2": "otro mensaje" }
   *   }
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String,Object>> handleValidation(MethodArgumentNotValidException ex) {
    Map<String, Object> body = new LinkedHashMap<>(); // Usamos LinkedHashMap para mantener orden de inserción.
    body.put("error", "Datos inválidos");

    // Mapa de errores por campo: nombreCampo -> mensajeDeValidación
    Map<String, String> fields = new LinkedHashMap<>();
    ex.getBindingResult().getFieldErrors()
        .forEach(fe -> fields.put(fe.getField(), fe.getDefaultMessage()));

    body.put("fields", fields);

    // Devolvemos 400 con el cuerpo construido.
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  /**
   * Maneja IllegalArgumentException lanzadas en la capa web/servicios cuando
   * se detecta un argumento inválido (por lógica manual).
   *
   * Respuesta:
   * - HTTP 400 (BAD_REQUEST)
   * - JSON simple con la causa:
   *   { "error": "mensaje de la excepción" }
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String,Object>> handleIllegalArg(IllegalArgumentException ex) {
    // Map.of crea un mapa inmutable con la clave "error" y el mensaje de la excepción.
    return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
  }
}
