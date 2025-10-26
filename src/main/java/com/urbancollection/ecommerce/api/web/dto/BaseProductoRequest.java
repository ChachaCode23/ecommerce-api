package com.urbancollection.ecommerce.api.web.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class BaseProductoRequest {

    @NotBlank
    @Size(max = 100)
    private String nombre;

    @Size(max = 500)
    private String descripcion;

    @NotNull
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que 0")
    private BigDecimal precio;

    @Min(value = 0, message = "El stock no puede ser negativo")
    private int stock;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}
