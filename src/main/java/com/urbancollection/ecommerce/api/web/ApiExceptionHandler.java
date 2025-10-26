package com.urbancollection.ecommerce.api.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("error", "Datos inválidos");

    Map<String, String> fields = new LinkedHashMap<>();
    ex.getBindingResult().getFieldErrors()
        .forEach(fe -> fields.put(fe.getField(), fe.getDefaultMessage()));
    body.put("fields", fields);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> handleIllegalArg(IllegalArgumentException ex) {
    return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
  }

  // (Opcional recomendado) JSON mal formado / cuerpo no legible
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Map<String, Object>> badJson(HttpMessageNotReadableException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(Map.of("error", "Cuerpo JSON inválido o no legible"));
  }
}
