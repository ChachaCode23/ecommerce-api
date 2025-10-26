// Este paquete es donde yo tengo las clases relacionadas a la capa web (controladores, manejo de errores, etc.)
package com.urbancollection.ecommerce.api.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @RestControllerAdvice
 * Esta clase es como un "catch global" para la API.
 * En lugar de yo estar haciendo try/catch en cada controlador,
 * aquí centralizo cómo voy a devolver los errores al cliente.
 *
 * La idea es que la respuesta de error salga limpia, clara
 * y parecida siempre (formato consistente).
 */
@RestControllerAdvice
public class ApiExceptionHandler {

  /**
   * Manejo de errores de validacion en los request.
   * Cuando un endpoint recibe un body con datos inválidos
   * según las validaciones (@NotNull, @Size, etc.) y Spring
   * lanza MethodArgumentNotValidException.
   *
   * devuelve
   * - status 400 (Bad Request)
   * - un JSON con un mensaje general
   * - otro JSON interno con los errores por campo, para que el frontend
   *   pueda decirle al usuario "el campo X falló por tal razón"
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {

    // body = lo que le vamos a mandar como respuesta al cliente
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("error", "Validación fallida");

    // fieldErrors = aqui yo voy guardando campo -> mensaje de error
    Map<String, String> fieldErrors = new LinkedHashMap<>();

    // Recorre todos los errores que disparó la validacion
    ex.getBindingResult().getFieldErrors().forEach(err -> {
      // err.getField() = nombre del campo que fallo
      // err.getDefaultMessage() = el mensajito de validación ("nombre es obligatorio", etc.)
      fieldErrors.put(err.getField(), err.getDefaultMessage());
    });

    //bloque dentro del body principal bajo la llave "fields"
    body.put("fields", fieldErrors);

    // Devuelve 400 con el JSON armado
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(body);
  }

  /**
   * Manejo de IllegalArgumentException.
   *
   * Esto es para cuando en la lógica de negocio o en el servicio
   * yo tiro un new IllegalArgumentException("mensaje").
   *
   * Ejemplo:
   *   if (cantidad <= 0) throw new IllegalArgumentException("Cantidad inválida");
   *
   * Aqui lo que hago es traducir esa excepción a una respuesta 400
   * clara para el cliente.
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> handleIllegalArg(IllegalArgumentException ex) {
    // Map.of(...) me deja hacer un map rápido tipo {"error": "mensaje..."}
    return ResponseEntity
        .badRequest()
        .body(Map.of("error", ex.getMessage()));
  }

  /**
   * Manejo de body JSON roto o ilegible.
   * Esto pasa cuando
   * - Cuando el cliente manda un JSON mal formado (coma de más, llave sin cerrar, tipo de dato raro).
   * - Cuando el tipo de dato no matchea (por ejemplo, te mando "abc" donde el backend esperaba un número).
   *
   * Spring en ese caso lanza HttpMessageNotReadableException.
   *
   * Aqui devolvemos un 400 con un mensaje entendible.
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Map<String, Object>> badJson(HttpMessageNotReadableException ex) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(Map.of("error", "Cuerpo JSON inválido o no legible"));
  }
}
