package com.urbancollection.ecommerce.api.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * ProductoCreateRequest
 * DTO de entrada para crear/actualizar productos desde la API.
 * Contiene los campos basicos que el cliente debe enviar.
 * Este DTO NO tiene logica de negocio, solo datos y validaciones basicas.
 * El Controller usa @Valid para activar estas validaciones automaticamente.
 * roductoService hara validaciones extra (reglas de negocio, duplicados, etc.).
 */
public class ProductoCreateRequest {

    @NotBlank                 // Nombre obligatorio, no null ni espacios.
    private String nombre;

    private String descripcion; // Campo libre, puede venir null

    @NotNull @Min(1)          // Precio requerido y minimo 1 (ver nota sobre BigDecimal).
    private BigDecimal precio;

    @Min(0)                   // Stock no negativo (0 o mayor).
    private int stock;

    // Getters / setters necesarios para el mapeo JSON -> objeto por Spring
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}
