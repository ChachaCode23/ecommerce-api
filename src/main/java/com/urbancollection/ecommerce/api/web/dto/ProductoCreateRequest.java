package com.urbancollection.ecommerce.api.web.dto;

/**
 * ProductoCreateRequest
 *
 * Este DTO se usa cuando se crea un producto nuevo (POST /api/productos).
 * Extiende de BaseProductoRequest, o sea, hereda:
 *  - nombre
 *  - descripcion
 *  - precio
 *  - stock
 * con todas sus validaciones (@NotBlank, @DecimalMin, etc).
 *
 * La idea de hacerlo así es reutilizar la validación y no repetir código.
 *
 */
public class ProductoCreateRequest extends BaseProductoRequest {
    // Por ahora no agregue nada nuevo, hereda todo de BaseProductoRequest.
}
