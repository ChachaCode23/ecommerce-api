package com.urbancollection.ecommerce.api.web.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * ProductoRequest
 * ------------------------------------------------
 * DTO para recibir datos de producto desde la API (crear/actualizar).
 * nombre obligatorio y no vacío (@NotBlank).
 * longitud máxima 255 caracteres (@Size(max=255)).
 * precio obligatorio y > 0 (@NotNull + @DecimalMin("0.01"))
 * stock no puede ser negativo (@Min(0)).
 * Este DTO solo transporta datos.
 * El controller usa @Valid para activar estas validaciones automaticamente.
 */
public class ProductoRequest {

    @NotBlank(message = "El nombre es obligatorio") // No null, no vacío, no espacios.
    private String nombre;

    @Size(max = 255, message = "La descripción no debe superar 255 caracteres") // Límite de tamaño para la descripción.
    private String descripcion;

    @NotNull(message = "El precio es obligatorio") // Debe enviarse un precio...
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que 0") // ...y debe ser > 0.
    private BigDecimal precio;

    @Min(value = 0, message = "El stock no puede ser negativo") // Stock mínimo 0.
    private int stock;

    // Getters y setters (necesarios para que Spring mapee JSON <-> objeto).
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}
