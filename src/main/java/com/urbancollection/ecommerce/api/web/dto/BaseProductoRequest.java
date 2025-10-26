package com.urbancollection.ecommerce.api.web.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * BaseProductoRequest
 *
 * Esta clase representa los datos básicos que yo necesito para crear o actualizar
 * un producto desde la API.
 *
 * La idea es que en vez de aceptar cualquier JSON sin control, yo defino exactamente
 * qué campos espero (nombre, descripción, precio, stock) y además le pongo validaciones.
 *
 * Esas validaciones las hace Spring automáticamente porque uso anotaciones como
 * @NotBlank, @DecimalMin, etc. Si el cliente manda algo inválido,
 * Spring tira un error de validación y yo puedo responder 400 (Bad Request)
 * con mensajes claros.
 *
 * Ventaja: protejo la lógica de negocio desde la entrada.
 * Ejemplo: no dejo que creen productos con stock negativo o precio en 0.
 */
public class BaseProductoRequest {

    /**
     * nombre:
     * - @NotBlank     → no puede venir vacío ni solo con espacios.
     * - @Size(max=100)→ límite de largo del nombre, para evitar texto muy extensos.
     *
     * Este sería el nombre comercial del producto, lo que ve el cliente.
     */
    @NotBlank
    @Size(max = 100)
    private String nombre;

    /**
     * descripcion:
     * - @Size(max=500) → solo limito el tamaño máximo.
     *   No lo marco como obligatorio porque un producto podría no tener una
     *   descripción larga todavía.
     *
     * Aquí va el texto descriptivo del producto.
     */
    @Size(max = 500)
    private String descripcion;

    /**
     * precio:
     * - @NotNull → el precio tiene que venir, no se puede dejar vacío.
     * - @DecimalMin("0.01") → el precio mínimo válido es 0.01.
     *     (Yo no permito precio 0 ni negativo.)
     * - message custom → este mensaje es el que le llega al cliente si rompe la regla.
     *
     * Uso BigDecimal en vez de double para evitar problemas de decimales (dinero).
     */
    @NotNull
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que 0")
    private BigDecimal precio;

    /**
     * stock:
     * - @Min(0) → no dejo que entren productos con stock negativo.
     *
     * Este campo representa cuántas unidades tengo disponibles en inventario.
     * Lo dejo como int.
     */
    @Min(value = 0, message = "El stock no puede ser negativo")
    private int stock;

    // Getters y setters estándar para que Spring pueda hacer el binding del JSON
    // que manda el cliente hacia este objeto BaseProductoRequest.
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}
