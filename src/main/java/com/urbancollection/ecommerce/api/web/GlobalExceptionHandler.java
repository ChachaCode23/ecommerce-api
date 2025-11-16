package com.urbancollection.ecommerce.api.web;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.urbancollection.ecommerce.api.web.dto.ApiErrorResponse;

@RestControllerAdvice(basePackages = "com.urbancollection.ecommerce.api.web")
public class GlobalExceptionHandler {

    // 400 - Errores de validación (@Valid en DTOs)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {

        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.toList());

        ApiErrorResponse body = new ApiErrorResponse(
                "Solicitud inválida",
                details
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // 400 - Errores de negocio simples (IllegalArgumentException)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {

        ApiErrorResponse body = new ApiErrorResponse(
                "Solicitud inválida",
                List.of(ex.getMessage())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // 404 - Recurso no encontrado (por ejemplo si el servicio lanza NoSuchElementException)
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(NoSuchElementException ex) {

        ApiErrorResponse body = new ApiErrorResponse(
                "Recurso no encontrado",
                List.of(ex.getMessage())
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
}
